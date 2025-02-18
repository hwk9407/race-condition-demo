# 쇼핑몰 재고 관리 시스템
## 프로젝트 개요
이 프로젝트는 Spring Boot와 Redis를 활용한 쇼핑몰 재고 관리 시스템으로, 장바구니, 상품, 주문(결제기능 없이)과 관련된 기능을 포함합니다. 개인 학습용 프로젝트로, **동시성 제어 및 성능 최적화**를 중점적으로 다루며, 이를 통해 효율적인 재고 관리 방안을 학습합니다.

---

## 주요 기능
1. 장바구니 기능 (Redis 기반 캐싱)
   - 유저별 장바구니 데이터를 Redis에 저장
   - `cart:{userId}` 형태로 저장되면, **30분 TTL** 설정
   - 상품 추가/제거 시 임시 재고와 장바구니의 TTL이 갱신되도록 구현
2. 임시 재고 관리
   - 장바구니에 상품 추가 시, 임시 재고(remaining_stock:{productId})를 별도로 관리
   - 상품별 임시 재고는 **2시간 TTL** 설정
   - 장바구니 갱신에 의한 TTL 갱신은 30분 이하로 내려갔을 시에만 TTL이 갱신되도록 로직 작성
3. 동시성 문제 해결
   - Redis 기반 분산 락을 활용하여 동시에 여러 사용자가 같은 상품을 장바구니에 담을 때 발생할 수 있는 Race Condition 방지 로직
   - 테스트 코드(`CartConcurrencyTest`)를 작성하여 동시에 500명의 사용자가 동시에 동일한 상품을 장바구니에 담을 때 재고가 음수가 되는 문제 확인
     - 초기 재고: 50개
     - 기대 결과: 임시 재고(`remaining_stock`)가 0이 되고 나머지는 수량 부족 예외 발생
     - 실제 결과: `remaining_stock = -450` (race condition 발생)
   - 문제 해결 방안
     - Redisson 기반 분산 락을 활용하여 동시성 문제 해결
     - 재고 감소 연산을 원자적으로 수행하여 Race Condition 방지
4. 장바구니 만료 시 이벤트
   - Redis Keyspace Notification을 활성화하여 TTL 만료 시 자동 이벤트 감지 및 처리
   - 장바구니 TTL 만료 시 Redis 메시지 리스너를 통해 이벤트를 감지하고, 만료된 장바구니 데이터를 정리

---

## 아키텍처 개요
![image01.png](images/image01.png)
- Spring Boot: API 서버
- Redis: 장바구니 및 임시 재고 관리
- MySQL: 상품 및 주문 정보 저장

---

## 트러블 슈팅 및 기술적 의사결정
- ### [쇼핑몰 장바구니 동시성 문제 해결을 위한 고민과 해결 과정](https://hwk-tech.tistory.com/16)
- ### [쇼핑몰 장바구니 캐싱 전략과 TTL 불일치 문제 해결 과정](https://hwk-tech.tistory.com/18)

---

## 프로젝트 범위 및 제외된 기능
이 프로젝트는 **동시성 제어 및 성능 최적화**에 초점을 맞춘 프로젝트입니다. 따라서, 다음과 같은 기능들은 구현 대상에서 제외하였습니다.

- 유저 도메인 및 인증 과정
  - 물론 장바구니 담기 기능에서 유저 정보를 활용해야 하지만, 인증 및 세션 관리를 직접 구현하는 대신, 간단하게 userId를 request body로 입력받는 형태로 구현
  - 동시성 제어 및 캐싱 전략에 집중하기 위해 선택이며, 실제 서비스에서는 토큰이나 세션 기반으로 인증이 진행된 요청에서 진행되어야 함
- 결제 및 주문 완료 로직 미구현
  - 현재는 장바구니 및 재고 관리에 집중하며, 결제 기능은 포함하지 않음  

















