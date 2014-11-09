1. 개발환경 (Development Enviroment)
a. 사용 컴퓨터 (Computer) : Mac OS X 10.9.5
b. 개발 툴(Development Tools) : 
 - 이클립스 Luna (Eclipse Luna)
 - 아파치서버 (Apache Server in http://www.serversfree.com/)
c. 개발 언어 : 
 - For App : Java with KakaoSDK, Android 4.2 API(Andorid SDK)
 - For remote database : PHP & MySQL
d. 설치 방법 : 
 - 이클립스 Luna (https://www.eclipse.org/에서 다운로드 후 설치)
 - 아파치서버
(1) 로컬 서버 (https://www.apachefriends.org/index.html에서 XAMPP 다운 후 사용)
(2) 실제 서버 (http://www.serversfree.com/ 에 계정 등록한 후 FileZilla로 사용)

---------------------------------------------------------------------
앱 실행 순서 설명

---------------------------------------------------------------------
카카오 아이디로 로그인 후 사용자관리 정보 틍록 및 대학생 인증

1. KakaoStroyLoginActivity 실행
(intent-filter => action "android.intent.action.MAIN")
2. KakaoStorySignupAcitivity 실행
(KakaoStoryLoginActivity의 onSessionOpened()에서 Intent 실행)

3. KakaoStorySignupActivity에서 로그인 시도
	Yes ->
MainAcitivity 실행
(KakaoStorySignupActivity의 redirectMainActivity()에서 Intent 실행)
	No ->
다시 로그인

4. MainActivity에서 해당 Kakao 아이디의 사용자정보를 사용자관리에서 불러오는 시도
대학교, 전화번호, 이메일, 학번이 입력되어있나?
	Yes ->
게시글을 공유할 것인지로 감
	No ->
NewKaCamProfileActivity 실행

5-1. (사용자관리의 데이타가 없는 상황) NewKaCamProfileActivity 실행
사용자정보 입력 후 UserManagement.requestupdateProfile()을 실행시키기 위해 requestUpdateProfile() 메소드 실행 (Kakao SDK의 함수)
사용자정보 업데이트가 성공 후 다시 MainActivity에 가서 "4"의 과정을 다시 수행 (Intent finish())

---------------------------------------------------------------------
원격 데이터베이스에 카카오스토리 글 보내기

5-2. (사용자관리의 데이타가 있는 상황) MainActivity에서 openAlert() 메소드 실행 
"당신의 포스팅을 카카오 캠퍼스에 공유하시겠습니까?"라는 메세지 생성
(a) 예를 클릭
putDataToRemoteActivity로 가서 카카오스토리에 올려져있는 게시글을 입력
* intent시 username을 같이 전송

(b) 아니오를 클릭
TabmenuActivity로 가서 읽고 싶은 컨텐츠 보기

6. (5-2에서 예를 클릭했을 시) Remote Database에 내 KakaoStory 글 입력하기
(a) 카카오스토리에 올릴 글(noteContent) 입력 후 Kakao SDK의 requestPost() 메소드를 사용하여 글을 입력 및 Last My Story ID를 가져오기
(b) lastMyStoryId를 사용하여 KakaoStoryService.requestGetMyStroies를 호출하기 위해 requestGetMyStories()메소를 호출
(c) KakaoStoryService.requestGetMyStories의 콜백함수로 받아 온 myStories변수에 있는 내용을 String.contain()메소드로 카테고리화하여 분류
(d) AsyncTask에서 jsonParser.makHttpRequest()메소드로 저장할 json type data를 POST로 보내서 저장
(e) POST 성공 후 TabmenuActivity로 이동

(PHP 부분)
6-A. input_data.php에서  
(a) POST 값으로 jsonMyStoriesInfo가 존재하면 Query문을 통해 데이터베이스에 값을 입력 (INSERT INTO .. VALUE ..)
(b) 값이 입력되었으면 response 변수에 success 및 그 외 태그에 값을 입력하여 json_encode로 값을 리턴

---------------------------------------------------------------------
원격 데이터베이스에 있는 글을 카카오캠퍼스에 올리기

7. (5-2에서 아니오를 클릭 or 6에서 정상적으로 값을 저장 했을 때) TabmenuActivity에서 읽기 원하는 정보를 클릭
 - '나두근두근해'를 클릭시 시나리오
	(a) TabmenuActivity에서 청춘수다(4번째 탭)을 클릭한다. (TabmenuActivity는 Action bar를 Tab모드로 사용하였습니다.)
	(b) 청춘수다(4번째) 탭에서 '나두근두근해'의 아이콘을 클릭한다. (클릭시 kind와 title을 전달하여서 사용합니다.)
	(c) '나두근두근해'를 클릭하여서 GetSelectedDataActivity로 이동합니다. (TabmenuActivity에서 받은 title과 kind를 사용합니다.)
	(d) Async에서 jsonParser를 사용하여 POST로 kind값을 보낸 후 원격 데이터베이스에서 json형태로 원하는 값을 리턴받습니다.
	(e) 원하는 데이터를 받은 후 ListActivity를 사용하기 위해 SimpleAdapter의 규격이 맞는 dataList에 데이터를 저장
	(f) Adapter를 실행시켜서 원하는 데이터를 List 형태로 출력

(PHP 부분)
7-A. get_selected_data.php에서
(a) POST 값으로 kind가 존재하면 Query문을 통해 데이터베이스에서 값을 출력(SELECT * FROM .. WHERE kind = ..)
(b) response 변수에 출력된 값을 저장 후 json_encode를 통해 값을 리턴

---------------------------------------------------------------------
카카오캠퍼스에서 글을 클릭하면 카카오스토리에서 글을 읽기

8. "7"을 수행한 후 List형태로 출력되어있는 데이터를 클릭 시 해당 웹 카카오스토리로 이동

