# EventSourcing

## 개요

이벤트소싱 기법은 와이어드컴퍼니 근무 당시 계정 서비스의 유저 모델에 대해 영속화 및 변경 이력을 함께 저장하기 위해 사용한 기법입니다.

이벤트소싱 기법은 모델의 상태를 저장하는 기법입니다.

일반적으로 잘 알려진 영속화 기법은 도메인 모델을 관계형 데이터베이스에 정규화 등을 통하여 저장 하거나 비정형 데이터베이스에 모델의 형태를 그대로 보존한 방식으로 저장하는것입니다.

그러나 이벤트소싱은 이와 다르게 도메인 모델의 상태를 변경한 다음 저장 하는 것이 아니라, 모델의 변경을 유발하는 모든 이벤트를 저장 합니다.

예를 들어 일반적인 서비스에서는 유저가 생성 된다면, 유저 객체를 생성하고 유저 테이블에 row 하나를 추가 합니다.

```java
public class UserService {
    public void createUser(String name, String email) {
        User user = new User(name, email);
        userRepository.save(user);
    }
}
```

**User**

| id | name | email          |
|----|------|----------------|
| 1  | John | john@email.com |

그리고, 유저의 이름을 변경한다면, 유저 객체의 이름을 변경하고 유저 테이블의 row를 업데이트 합니다.

```java
public class UserService {
    public void changeUserName(Long userId, String name) {
        User user = userRepository.findById(userId);
        user.setName(name);
        userRepository.save(user);
    }
}
```

**User**

| id | name | email          |
|----|------|----------------|
| 1  | Jane | john@email.com |

그러나, 이벤트 소싱 기법 에서는 모델의 상태를 변경하여 저장하지 않습니다. 모델의 상태를 변경 유발하는 이벤트를 발생 시키고 해당 이벤트를 저장 합니다.

```java
public class UserService {
    public void createUser(String name, String email) {
        User user = new User(name, email);
        userRepository.save(user);
        eventStore.save(new UserCreatedEvent(user.getId(), user.getName(), user.getEmail()));
    }

    public void changeUserName(Long userId, String name) {
        User user = userRepository.findById(userId);
        user.setName(name);
        userRepository.save(user);
        eventStore.save(new UserNameChangedEvent(user.getId(), user.getName()));
    }
}
```

**StreamEvent**

| id | type            | stream_id                            | data                                   |
|----|-----------------|--------------------------------------|----------------------------------------|
| 1  | UserCreated     | d290f1ee-6c54-4b01-90e6-d701748f0851 | { name: John, email: "john@email.com"} |
| 2  | UserNameChanged | d290f1ee-6c54-4b01-90e6-d701748f0851 | { name: Jane}                          |

저장된 이벤트 스트림을 다시 재생 하면, 모델을 복원할 수 있습니다.

## 특징

### 장점
원본 데이터를 훼손하지 않거나, 모델의 변경 이력을 보관 하여 추적이 쉽고 감사에 용이하다는 너무 잘 알려진 장점을 제외하고, 개발간 겪었던 실제 장점은 다음과 같습니다.

우선 이벤트소싱 방식은, 소프트웨어 시스템이 더욱 더 현실과 가깝게 구현됩니다. 실제 도메인에서 발생하는 명령(command) 를 바탕으로 발생하는 부수효과(event)를 그대로 저장 하기 때문입니다.


## EDA(Event-Driven Architecture)와의 차이점



(TBD)

