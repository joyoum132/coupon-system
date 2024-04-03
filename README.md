# 실습으로 배우는 선착순 이벤트 시스템
[강의 정보](https://www.inflearn.com/course/%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%8B%A4%EC%8A%B5/dashboard)

<aside>
💡 선착순  쿠폰 발급 시스템을 만들어 나가면서, 각 단계에서 어떠한 문제점이 발생하고 이를 어떻게 해결하는지 학습

- 과도한 트래픽 발생, 동시성 이슈를 해소하기 위한 방안 제시
- 실행 환경 : SpringBoot + Java + Docker + **Redis** + **Kafka**
</aside>


## 시나리오
선착순 100명에게 할인 쿠폰 발급
```java
public void apply(Long userId) {
        long count = couponRepository.count();

        if(count > 100) return;

        couponRepository.save(
                new Coupon(userId)
        );
    }
```

### 문제점1 : Race Condition
> thread 를 1000개로 늘려 apply 를 호출하면 Count 테이블에는 100개 이상의 쿠폰 정보가 저장되어있음

해결1 : **쿠폰 발급 개수 조회를 위해 redis 사용(동시성 제어)**
- single thread 이기때문에 경쟁 상태 발생하지 않음
- 메모리 데이터베이스로 조회, 수정 성능 빠름 : O(1)
- incr 명령어로 쿠폰 발급 개수를 순차적으로 증가시킬 수 있음
</br>
</br>

### 문제점2 :  트래픽이 몰리면 DB처리 지연, 다른 API 영향
> DB에서 감당 가능한 수준을 넘어서는 경우 처리 대기, 다른 API 역시 처리 대기 발생

해결2: **이벤트 분산 위해 Kafka 사용(처리량 조절)**
- producer(API 모듈) 에서 쿠폰 발급 가능한 사용자를 토픽에 전달
- consumer(Consumer 모듈) 에서 토픽에 저장된 사용자 아이디를 이용해 쿠폰 발급
</br>
</br>

### 문제점3 : 쿠폰이 중복 발급될 수 있음
> 100명의 유저에게 골고루 발급되어야 하는데 한 회원에게 중복되어 발급이 가능한 코드

해결3: **redis 에 쿠폰을 발급한 사용자 아이디를 저장, set 자료구조 이용**
- 입력 순서를 유지
- 중복 허용하지 않음