# 정진석 - 포트폴리오
> 경력 사항은 [여기](https://github.com/jin3378s)에서 확인 하실 수 있습니다.

> 본 레포지토리에 언급된 내용은 [해당 레포지토리](https://github.com/gyuwon/ArchitecturalRunwayGuidelines)의 깊은 영향을 받았습니다. 해당 내용을 실제로 이행 했던 개발자로서 이해 하고 느꼈던 점을 최대한 정리하여 작성 하고자 합니다.

스타트업의 초기 개발자로 합류해 [transactional script](https://martinfowler.com/eaaCatalog/transactionScript.html) 방식으로 코드를 작성하던 저는 이후 팀의 규모 커지고, 비지니스가 복잡해 짐에 따라 코드를 맥락에 따라 분리하고, 역할과 책임에 따른 코드 분리가 필요하다 느꼈고 그간 여러 논의와 배움을 토대로 지향하고자 하는 아키텍쳐와 사용 가능한 기법들을 정리 하였습니다.

이러한 기법은 조직의 규모와 마주하고 있는 비지니스 복잡도에 따라서 적합 할수도, 적합하지 않을 수 있습니다.

여태까지의 경험을 토대로 할 때 이러한 기법은 단일 스쿼드 팀 규모(제품팀 전체 10인 미만)에는 적합하지 않을 수 있습니다. 최소 2개의 스쿼드가 동시 운영 될 때 적합 할 수 있습니다.

## 고수준 아키텍처

서버 애플리케이션 고수준 아키텍쳐는 다음과 같은 모듈로 구성 됩니다.

- domain-models
- gateways
- foundation

### domain-models

시스템의 도메인 모델이 포함 됩니다. 각 모델은 단일 호스팅 전략을 채택하거나 공유 호스팅 전략을 채택 할 수 있습니다.

**도메인 모델**

도메인 모델은 세부 모듈로 구성 됩니다.

| 모듈          | 설명                                                                                                |
|-------------|---------------------------------------------------------------------------------------------------|
| application | 인터페이스를 프로세스에 올릴 수 있는 코드가 위치 합니다. SpringBootApplication의 메인함수, HttpServer, GrpcServer 등이 위치 합니다.   |
| api         | 도메인 모델을 외부 프로세스에서 호출하기 위한 인터페이스를 제공합니다. RestController 등을 포함 합니다.                                 |
| models      | 도메인을 추상화 한 모델을 포함 합니다. 해당 모듈에 포함된 모델은 외부로 직접 노출 되지 않습니다. 외부 노출이 필요한 경우라면 contracts 모듈을 통해 노출 됩니다. |
| contracts   | 도메인 모델 중 외부로 노출 될 모델을 포함 합니다. 명령 모델, 열거형 타입 등이 포함 될 수 있습니다.                                       |
| adapters    | 도메인 모델의 추상화 중 구체적인 구현을 포함 합니다. 데이터베이스 구현체, 외부 서비스 SDK 등이 포함 될 수 있습니다.                             |
| unit-tests  | 실제 배포 환경이 아닌, 코드 조각을 대상으로 작성하는 테스트의 모음입니다. 서비스레이어 테스트, adapter 구현체 테스트 등이 추가 될 수 있습니다.            |


**단일 호스팅 전략**

모델에 대한 오픈된 인터페이스를 독립된 프로세스에 호스팅 하는 방식입니다. 단일 호스팅 전략을 취하는 도메인 모델은 다음과 같은 모듈 구조를 가집니다
> [여기](./springframework/domain-models/identity)에서 확인 할 수 있습니다.
```
- domain-model
  - application
  - api
  - contracts
  - models
  - adapters
  - unit-tests
```

**공유 호스팅 전략**

구분 된 맥락 여러개를 하나의 프로세스에 호스팅 하는 방식 입니다. 아직 단일 호스팅 전략이 필요하지 않은 경우에 활용 합니다. 논리적으로 구분된 맥락은 필요에 따라 언제든지 호스트를 분리할 수 있습니다.
> [여기](./springframework/domain-models/catalog)에서 확인 할 수 있습니다.
 
```
- domain-model
    - api
    - contracts
    - models
    - adapters
    - unit-tests
- shared-hosts
    - application
```
    
## Gateways

도메인 모델을 클라이언트에게 제공하기 위해 게이트웨이 패턴을 활용 합니다. 게이트웨이는 클라이언트에게 적절한 서비스의 위치를 안내 할 수 있도록 Gateway Routing Pattern을 구현하고, 각 호스트별 중복 구현 되어 있는 기능을 공통화 하는 Gateway Offloading Pattern을 구현할 수 있습니다. 각 클라이언트를 위한 BFF와 외부시스템 연동을 위한 Gateway 등으로 구성 될 수 있습니다.

### BFF(Backend For Frontend)
UI 시스템을 위한 백엔드 서비스를 제공하는 게이트웨이 성격의 서비스 입니다. BFF는 분산환경 백엔드를 캡슐화 하여 백엔드의 변경이 프론트엔드로 전파 되는것을 최대한 방지 합니다.
### Gateway

## Foundation
도메인 모델에서 공통으로 사용 할 수 있는 추상화 혹은 라이브러리 등이 포함됩니다.

## 멀티모듈 백엔드 애플리케이션

멀티 모듈 백엔드 애플리케이션을 지향하는 이유는 단일 모듈 어플리케이션에서 발생 할 수 있는 여러 문제들을 구조적으로 방지하기 위함 입니다.

우선, 백엔드 애플리케이션은 역할에 따라 여러 계층으로 나뉘게 됩니다. hexagonal architecture 등과 같이 프레젠테이션 레이어에서 도메인 레이어로 덜 중요한 것들이 더 중요한 것에 의존하게 되고 인프라스트럭쳐 계층의 경우에는 제어의 역전(IoC)을 통해 도메인 지식이 부가적인 지식에 의존하지 않도록 합니다. 

그러나, 단일모듈 어플리케이션은 개발자가 작성한 도메인 지식이 인프라스트럭쳐의 구체적인 구현체를 의존 하는 경우를 컴파일 단계에서 제한 하기 어렵습니다. 또한 IDE의 편리한 자동완성 기능등의 도움을 받았을 때 나도 모르게 의존성이 잘못 설정 되어버리는 문제가 발생 할 수 있습니다.

또한, 서로 다른 맥락을 가진 도메인 모델간 코드 직접 참조를 가능하게 합니다. 알게모르게 설정한 의존성이 서비스간 독립성을 떨어뜨립니다.

멀티모듈 백엔드 애플리케이션은 위와 같은 문제를 높은 확률로 차단 합니다. 모듈 관리 명세에 의존성 모듈로 대상 모듈을 추가한 경우에만 참조 할 수 있으며 이는 코드를 읽는 사람으로 하여금 모듈간 의존성에 대한 높은 가시성을 제공 합니다.

멀티모듈 백엔드 애플리케이션을 gradle로 구성하는 방법은 [여기](./springframework)에서 확인 하실 수 있습니다.

## 이벤트 소싱
이벤트소싱과 관련된 경험은 [여기](./springframework/foundation/eventsourcing/README.md)서 확인 할 수 있습니다. 