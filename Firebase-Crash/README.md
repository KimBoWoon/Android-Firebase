# Firebase-Crash

* 옛날에는 ```google-services.json```를 넣고 Gradle에 ```compile 'com.google.firebase:firebase-crash:11.0.4'```만 추가하면 자동으로 에러가 발생했을 때 메일을 보내주거나 자료를 수집했는데 지금은 fabric이라는 서비스가 생겨서 이쪽을 사용하라고 한다.

## Fabric
* 패브릭(Fabric)은 안정성, 사용자 확보, 수익성, 사용자 인증의 이슈들을 해결해 줄 세 가지의 모듈
* 패브릭은 크래시리틱스(Crashlytics), 모펍(MoPub), 트위터 등의 서비스를 통합해 개발자들이 더 안정적인 앱을 만들도록 돕습니다.
* 라고 하는데 지금은 크래시리틱스(Crashlytics)를 사용해보겠습니다.

## 사용법
* [패브릭 홈페이지](https://get.fabric.io/)로 이동하여 회원가입을 합니다. 회원가입은 간단합니다. 자신이 사용하고 있는 이메일을 아이디로 하며 이메일 인증을 받으면 바로 회원가입이 되며 사용이 가능합니다.
* 그리고 자신이 사용하는 IDE를 선택합니다. xcode, android studio, intellij, unity가 있습니다.
* 저는 android studio를 선택했습니다. android studio의 ```file -> setting -> plugins``` 메뉴로 들어가 ```browse repositories...```을 선택합니다. 여기서 ```fabric for android studio```를 선택해 플러그인을 설치하고 android studio를 다시 시작합니다.
* 그러면 상단에 Fabric메뉴가 생성됩니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/android-studio-menu.png)
 
* 클릭하면 다음과 같이 나오고 회원가입한 이메일로 로그인을 합니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/plugin-install-success.png)
 
* 로그인이 완료되면 Fabric이 제공하는 다양한 모듀을 클릭 몇 번 으로 간단하게 사용할 수 있습니다. 여기서 Crashlytice를 사용해보겠습니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/fabric-menu.png)

* Crashlytice를 선택하면 다음과 같이 나오며 다양한 정보를 확인할 수 있고 오른쪽 위에 install을 선택하면 설치할 수 있는 메뉴로 이동합니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/fabric-crash-menu.png)
 
* java와 java + NDK를 사용할 수 있으며 오른쪽 아래 Apply버튼을 누르면 자동으로 코드가 삽입되며 Gradle Sync버튼을 누르면 Gradle Build를 진행합니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/fabric-crash-install.png)

* 빌드가 완료되면 [Fabric Dashboard](https://fabric.io/home)에 사용자가 등록한 앱이 나타나며 어떤 에러가 발생했는지 알아볼수 있습니다.
![Fabric](https://github.com/KimBoWoon/Android-Firebase/tree/master/Firebase-Database/images/fabric-dashboard.png)

## 참고
* https://blog.twitter.com/official/ko_kr/a/ko/2014/introducing-fabric-kr.html
* http://redbyzan.github.io/writing/android-fabric/

# 궁금한점은 이슈에 남겨주세요 같이 토의해봅시다.