## **main 브랜치 설계 의도**

### **1) OrderController의 Member 파라미터**

본 서비스는 “회원 인증이 존재한다”는 가정하에 구현했습니다.

따라서 OrderController는 아래 형태로 Member member를 입력으로 받습니다.

- 파일: src/main/java/com/pay/polycube/controller/OrderController.java
- 메서드: order(@Valid @NotNull Member member, @Valid @RequestBody OrderRequest req)

현재는 인증이 없기 때문에 정상적인 동작을 위해선 별도의 바인딩/리졸버가 필요합니다.

다만 확장 시 스프링 시큐리티의 @AuthenticationPrincipal로 자연스럽게 연결되도록, **Member를 요청의 일부로 취급하지 않고 “인증 주체”로 받는 형태**를 유지했습니다.

---

### **2) 정적 팩토리 메서드로 생성 진입점 단일화**

Order 엔티티는 필드가 늘어날수록 생성 조건/초기화 규칙이 복잡해질 수 있습니다.

그래서 생성자를 private/protected로 제한하고 create() 정적 팩토리 메서드로 **생성의 단일 진입점**을 만들었습니다.

- main: Order.create(member, productName, originalPrice)
- 파일: src/main/java/com/pay/polycube/domain/Order.java (main 커밋 기준)

이 방식의 목적은 다음과 같습니다.

- 생성 규칙을 한 곳에 모아 **불완전 상태 객체 생성 방지**
- 생성 파라미터가 늘어날 때도 “어디서 생성되는지”를 **명확하게 유지**
- 도메인의 생성 시점에 필요한 최소 조건을 강제

---

### **3) 할인 정책 다형성 (전략 패턴)**

요구사항에 따르면 할인 정책은 수시로 추가/변경 가능합니다.

따라서 인터페이스를 두고(DiscountPolicy), 구현체가 실제 할인 로직을 담당하도록 구성했습니다.

- 파일: src/main/java/com/pay/polycube/policy/DiscountPolicy.java (main: discount(Grade grade, int price))

이를 통해 Order 도메인은 “정책 구현체”를 몰라도 할인을 적용할 수 있으며,

정책 변경 시 서비스/도메인 변경 범위를 최소화할 수 있습니다.

---

### **4) 책임 분리(SRP): 서비스 오케스트레이션 + 도메인 로직**

서비스는 오케스트레이션에 집중시키고, 비즈니스 규칙은 도메인에 배치했습니다.

예시 흐름:

1. 주문 생성
2. 할인 적용
3. 결제 처리
4. 저장/반환
- 파일: src/main/java/com/pay/polycube/service/OrderCommandService.java (feature에서 더 명확해짐)

“할인 적용”은 주문의 핵심 책임이라기보다 **정책 변화의 영향을 크게 받는 영역**으로 판단하여,

전략 패턴을 통해 도메인 바깥(Policy)에서 변동성을 격리했습니다.

---

### **5) 데이터 불변성/캡슐화 강화**

불필요한 @Setter를 제거하고, 도메인 상태 변경은 의도된 메서드로만 가능하도록 제한했습니다.

- 예: 결제 완료 시각(paidAt)은 pay() 호출을 통해서만 세팅

---

## **feature 브랜치 설계 개선과 논리적 근거**

추가 요구사항(B)을 적용하면서 main 설계를 그대로 확장하면, 다음 문제가 발생합니다.

- 할인 정책이 늘어날 때 정책 조합/순서(우선순위)가 중요해짐
- 결제 완료 주문은 “적용된 정책의 스냅샷”이 남아야 함
- “포인트 결제 시 5% 추가 할인”은 등급 할인 이후 적용되는 것이 요구됨(순서 필요)

따라서 feature에서는 **정책 조합/순서/이력 보존**을 중심으로 설계를 개선했습니다.

---

### **1) Config class 도입: 정책 조합과 순서의 명시적 관리**

할인 정책이 복잡해지면 “정책 단일 적용”보다 “정책 조합(Composite)”이 필요해집니다.

feature에서는 DiscountPolicyConfig에서 정책 조합과 순서를 관리합니다.

- 파일: src/main/java/com/pay/polycube/policy/DiscountPolicyConfig.java
- 구성:
  - CompositeDiscountPolicy(List.of(new GradeDiscountPolicy(), new PaymentMethodDiscountPolicy()))

즉, 정책의 적용 순서를 **코드 내부의 암묵적 규칙이 아니라 설정(구성)에서 명시**했습니다.

정책이 늘어나도 도메인/서비스 코드를 바꾸지 않고, “정책 조합”만 바꾸면 됩니다.

---

### **2) 도메인 - 정책 분리: Order는 “적용/기록”, 정책은 “계산”**

main에서는 Order.discount(DiscountPolicy) 형태로 Order가 정책에 직접 의존했습니다.

feature에서는 이를 제거하여 다음처럼 책임을 분리했습니다.

- 정책(Policy): “할인 금액 계산” 담당
- 도메인(Order): “결과 반영 + 이력 기록” 담당

구체적으로 Order는 아래 메서드로 결과를 기록합니다.

- 파일: src/main/java/com/pay/polycube/domain/Order.java
- applyDiscount(String policyName, int finalPrice)
  - policyName 기록
  - finalPrice 기록
  - discountPrice(할인액) 계산/저장
  - discountRate(할인율) 계산/저장

이로써 정책이 바뀌어도 주문 자체는 “이미 결제 완료된 스냅샷 데이터”를 유지합니다.

### **2-1) 테스트 전략 개선 – 순수 유닛 테스트로의 전환**

기존 테스트는 OrderService를 통해 할인 정책을 간접적으로 검증하는 구조였다.
기존 코드 구조는 Order가 DiscountPolicy를 직접 호출하여 할인 로직에 관여하였기 때문이다.
이 방식은 실제 서비스 흐름을 검증하는 데에는 유용하지만, 할인 정책 자체의 동작과 우선순위를 독립적으로 검증하는 데에는 한계가 있었다.
특히, 서비스 클래스는 저장소(Repository)와 정책(DiscountPolicy)을 동시에 의존하기 때문에, 테스트 실패 시 원인이 정책 로직인지, 서비스 로직인지, 혹은 저장소 연동인지 명확히 분리하기 어려웠다.
도메인 정책 분리로 인해 순수한 유닛 테스트 작성이 용이해졌다.
이에 따라 할인 정책의 조합과 적용 순서를 보다 명확히 검증하기 위해, 정책 객체를 직접 호출하는 순수 유닛 테스트로 구조를 개선하였다.

---

### **3) 할인 정책 파라미터 변경: OCP 관점의 개선**

main의 정책 인터페이스는 discount(Grade grade, int price)처럼 필요한 값만 전달했습니다.

하지만 feature 요구사항에서는 결제수단/등급/주문 정보 등 정책에 필요한 입력이 늘 수 있어,

정책 변화마다 인터페이스 시그니처가 흔들릴 위험이 있습니다(OCP 위반).

그래서 feature에서는 다음 방식으로 변경했습니다.

- 파일: src/main/java/com/pay/polycube/policy/DiscountPolicy.java
- int discount(Order order, int currentPrice)
- String getName()

정책은 Order에서 필요한 값만 getter로 사용하고, 현재 가격(currentPrice)을 받아 단계적 할인(중복 할인)을 구현합니다.

즉,

- 인터페이스는 “안정화”되고
- 정책 구현체가 늘어도 서비스/도메인 변경이 최소화됩니다.

---

### **4) 중복 할인(포인트 5%)의 우선순위 보장**

feature 요구사항은 “포인트 결제 시 최종 금액에서 추가 5%”입니다.

따라서 등급 할인 → 결제수단 할인 순서가 보장되어야 합니다.

- CompositeDiscountPolicy는 전달받은 List<DiscountPolicy> 순서대로 적용합니다.
- Config에서 순서를 명시하여 요구사항을 코드로 고정했습니다.

---

### **5) 정책 변경 후에도 과거 결제 이력 보존 테스트**

feature에서는 “정책이 변경/삭제되더라도 과거 주문 데이터는 보존되어야 한다”를 테스트로 증명합니다.

- 파일: src/test/java/com/pay/polycube/domain/HistoryTest.java
- 검증 항목:
  - grade, policy, originalPrice, finalPrice, discountPrice, paymentMethod, paidAt 등이 DB에 저장된 값으로 유지됨
  - 이후 정책을 바꿔도(새 DiscountPolicy로 서비스 구성) 과거 주문 데이터는 변하지 않음

이 테스트는 “정책 변경이 잦은 운영 환경”에서 데이터 정합성을 어떻게 보장하는지 보여주는 근거입니다.

---

## **한계 및 향후 개선**

- 현재 Member는 인증 연동 전제로 컨트롤러 파라미터로 유지되어 있으며, 실제 동작을 위해서는
  - @AuthenticationPrincipal 적용 또는
  - 커스텀 Argument Resolver 도입이 필요합니다.
- 할인 정책의 “이름/설명/버전” 등은 향후 정책 테이블(DB) 기반으로 확장 가능하며,

  현재는 과제 범위 내에서 **주문 엔티티에 정책 스냅샷을 저장**하는 방식으로 요구사항을 충족했습니다.