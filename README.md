#  또레카

어떤 상품이 마음에 드는지 헷갈리는 사용자들을 위한

***'감정분석 + 상품추천 쇼핑몰 : Eureka'*** **리펙토링 프로젝트** 

#### 🛠개발기간 : 2021.04.12~2021.05.21

 [Eureka ](https://github.com/sumin416/Eureka)
 
 <br>
 <br>
 
## Our Team
### Backend
* 팀장 이수민([@sumin416](https://https://github.com/sumin416 "github link"))

* 팀원 심재혁([@JaeHyukSim](https://github.com/JaeHyukSim "github link"))

### Frontend
* 팀원 강구원([@KangGooWeon](https://github.com/KangGooWeon "github link"))

* 팀원 정준영([@dkwjdi](https://github.com/dkwjdi "github link"))
 <br><br>



 
### 🛠개발기간 
### 2021.04.12~2021.05.21


<br><br>

## 💡유레카 기능

#### ◾메인페이지
![메인페이지](https://user-images.githubusercontent.com/42925284/119686684-8b2d6a00-be81-11eb-9059-c50ac6598ae2.gif)

#### ◾카테고리 조회
![카테고리 조회](https://user-images.githubusercontent.com/42925284/119686807-aa2bfc00-be81-11eb-8fd4-d898f32b47e7.gif)

#### ◾상품조회, 감정, 장바구니 등록
![디테일 페이지](https://user-images.githubusercontent.com/42925284/119686865-b31ccd80-be81-11eb-9f4b-5ba8133b7588.gif)

#### ◾장바구니 조회 및 결제
![결제](https://user-images.githubusercontent.com/42925284/119687287-098a0c00-be82-11eb-9d1f-831f293c89e7.gif)

#### ◾내가 본 상품 감정 통계
![마이유레카](https://user-images.githubusercontent.com/42925284/119687449-30e0d900-be82-11eb-962b-b43ac15c4d31.gif)

#### ◾마이페이지
![마이페이지](https://user-images.githubusercontent.com/42925284/119687491-38a07d80-be82-11eb-9711-e7025ff0ddd7.gif)

### ◾판매자페이지

#### ◾상품등록
![상품등록](https://user-images.githubusercontent.com/42925284/119688034-9d5bd800-be82-11eb-83d0-35c7de04b6a6.gif)

#### ◾판매제품관리
![판매제품관리](https://user-images.githubusercontent.com/42925284/119688075-a3ea4f80-be82-11eb-95dd-d12164c1f49c.gif)

#### ◾주문알림
![주문알림](https://user-images.githubusercontent.com/42925284/119688099-a77dd680-be82-11eb-9af8-6a8aeafcb720.gif)

#### ◾배송관리
![배송관리](https://user-images.githubusercontent.com/42925284/119688119-ac428a80-be82-11eb-9cae-c5f08385a52d.gif)




<br><br>


## 기술 스택

 ***WEB***

**DevOps**
||기술 스택|
|--|--|
|SCM|GitLab,Jira|
|Cloud Service|AWS EC2|

<br>

**Front-end**
||기술 스택|
|--|--|
|Language|HTML CSS JavaScript|
|Framework|Vue.js|
|Library|Vuetify Axios chart.js|

<br>

**Back-end**
||기술 스택|
|--|--|
|Server Architecture|MSA|
|Language|Java8|
|Framework|SpringBoot SpringCloud SpringSecurity SpringDataJPA|
|BuildTool|Gradle,Maven|
|DBMS|MySQL|
|Testing | JUnit Locust|

<br>


## Frontend 실행방법 및 파일구조
[Frontend](/frontend/README.md)
## Backend 실행방법 및 파일구조
[Backend](/backend/README.md)


## Frontend Refactoring

#### 1.서버에 가해지는 디도스 공격 해결 (for문을 통한 Axios호출) 
<br><br>
기존의 코드는 for문을 통해 Axios통신을 여러번 한 뒤, response값에 대해 다시 for문을 돌려 Axios를 호출하는 구조
Backend에서의 Database통신은 저렴하지만, 프론트에서 백으로의 통신은 비용이 비쌈 => 백엔드에서 한 번에 필요한 데이터를 받는 구조로 변경
![디도스1](https://user-images.githubusercontent.com/57394523/119693915-b87d1680-be87-11eb-9684-c4e1d876cac5.png)


<br><br>
메인 화면에서 오늘의 상품, 요즘 뜨는 상품, 스테디셀러 상푸미 카테고리별 추천상품별로 총 4개의 컴포넌트에서 각각 사진에 대해 Axios통신을 하는 구조
한번의 Axios를 통해 4개의 컴포넌트에 필요한 데이터를 받은 후 Vuex를 통해 상태관리하는 구조로 변경
![디도스2](https://user-images.githubusercontent.com/57394523/119694276-0c87fb00-be88-11eb-852e-f7dc675d41b9.png)
![디도스2해결](https://user-images.githubusercontent.com/57394523/119694283-0eea5500-be88-11eb-9d5e-b7a4dad73eef.png)




#### 2.유지보수가 어려운 코드를 해결
<br><br>
기존의 코드는 각각의 컴포넌트에서 Axios를 직접호출 하였기 때문에 하나의 API를 수정하기 위해서는 여러 컴포넌트에서의 코드수정이 필요
이를 위해 API를 모듈화해 유지보수를 쉽게 하였고 중복으로 일어나는 Axios호출 코드를 request객체에 저장해 코드의 중복을 제거
<br>
![문제1](https://user-images.githubusercontent.com/57394523/119694562-48bb5b80-be88-11eb-9629-941139816ad7.png)
![해결](https://user-images.githubusercontent.com/57394523/119694658-62f53980-be88-11eb-941b-4d6928cfe70a.png)



