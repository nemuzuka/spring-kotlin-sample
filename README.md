# SpringBoot + Kotlin + Vue.js Sample

SpringBoot と Kotlin と Vue.js を組み合わせたサンプル

## OpenID Connect

このアプリは、Google の OpenID Connect でログインします。
[このサイト](https://developers.google.com/identity/protocols/OpenIDConnect) で設定してください。

承認済みのリダイレクト URI には `http://localhost:8089/login/oauth2/code/google` を指定します。

その後、以下の環境変数の設定をしてください。

* `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID`
    * クライアントID
* `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET`
    * クライアントシークレット

## Repository のテスト

[FlywayTest](https://github.com/flyway/flyway-test-extensions) を使用してます。

* [JdbcTaskRepositoryTest](src/test/kotlin/net/jp/vss/sample/infrastructure/tasks/JdbcTaskRepositoryTest.kt)
    * 実際に RDBMS(H2) へアクセスしてテストします

## Service のテスト

[Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) を使用してます。

* [CreateTaskUseCaseImplTest](src/test/kotlin/net/jp/vss/sample/usecase/tasks/CreateTaskUseCaseImplTest.kt)
    * Repository の呼び出しがありますが、Mock を使用するので RDBMS へアクセスしません

## Controller のテスト

* [CreateTaskApiControllerTest](src/test/kotlin/net/jp/vss/sample/controller/tasks/CreateTaskApiControllerTest.kt)
    * UseCase の呼び出しがありますが、Mock を使用するので RDBMS へアクセスしません
    * テストできるのはメソッド呼び出し以降です。`@Valid` の validate 等はこのテストではできません
    * Spring Security を使用して CSRF 対策をしているので、リクエスト時に `with(SecurityMockMvcRequestPostProcessors.csrf())` を指定します

## Integration のテスト
* [CreateTaskApiIntegrationTest](src/test/kotlin/net/jp/vss/sample/controller/tasks/CreateTaskApiIntegrationTest.kt)
    * `@SpringBootTest` を使用することで、本物の Bean を Injection して実行します
    * API は Spring Security を off にし、MockMvc を使用して IntegrationTest を実施しています
        * TestRestTemplate は使用していません


## ktlint

### フォーマット + ktlint

push する前に実施することを想定

```sh
$ ./gradlew formatAndKtlint
```

### ktlint のみ実行

CI 回す時にチェックする(エラーがある場合、CI が通らない)

```sh
$ ./gradlew ktlint
```

## ローカル起動

初回だけ実行してください

```sh
$ cd src/main/resources
$ cp _sample_application-personal.properties application-personal.properties
```

コピーした `application-personal.properties` の情報(ex. RDBMS の接続先)を変更してください

起動は以下のコマンドを使用します

```sh
$  ./gradlew -is bootRun -Ppersonal
```

以下のような情報が出るまで待ってください(Started VssApplicationKt)
```sh
2019/04/30 09:25:39.384 [main] INFO  net.jp.vss.sample.VssApplicationKt:61 - Started VssApplicationKt in 2.719 seconds (JVM running for 3.165)
```

起動した後、以下のコマンドを叩いてレスポンスがくれば起動成功です。

```sh
$ curl -H 'Content-Type:application/json' http://localhost:8089/api/health
```

```
It's work!
```

## IntelliJ で動作確認

https://github.com/domaframework/kotlin-sample の設定を行ってください

## CircleCI での docker image の build

### 必要な環境変数

* `AWS_ACCESS_KEY_ID`
    * ECR の接続に必要
* `AWS_SECRET_ACCESS_KEY`
    * ECR の接続に必要
* `AWS_DEFAULT_REGION`
    * ECR の接続に必要
* `ECR_ENDPOINT`
    * e.g. `<ACCOUNT-ID>.dkr.ecr.ap-northeast-1.amazonaws.com`
* `REPOSITORY_NAME`
    * リポジトリ名
    * `${ECR_ENDPOINT}/${REPOSITORY_NAME}` となるように設定します

### 基本ポリシー

1. feature ブランチの build 時には docker image を作成しない
    * feature で始まる必要があります
2. develop ブランチの build 時に docker image を作成する
    * tag は `${REPOSITORY_NAME}:${CIRCLE_BUILD_NUM}`
3. master ブランチの build 時に docker image を作成する
    * tag は `${REPOSITORY_NAME}:${CIRCLE_BUILD_NUM}`
4. git の tag を打った時に docker image を作成する
    * tag は `${REPOSITORY_NAME}:${CIRCLE_TAG}`
    * V で始まる必要があります

<!--
CI 側で Docker Image を作成しているのは
com.palantir.docker を使って Docker Image が作れなかったから...
-->

<!--
JDK が 8 なのは、alpine が 8 までしか出してないから(サイズがでかすぎる)
-->

## Heroku に deploy する

### 1. Heroku ログイン

```
$ heroku login
$ heroku container:login
```

### 2. heroku アプリ作成

```
$ heroku create
```

アプリの name を保持しておいてください
ex. radiant-brook-99999

### 3. postgres 立ち上げ

```
$ heroku addons:create heroku-postgresql:hobby-dev -a ${アプリのname}
```

[コンソール](https://data.heroku.com/)から

- Host
- Database
- User
- Port
- Password

がわかるので、

Heroku 上の管理コンソールから以下の環境変数を設定します。

- SPRING_DATASOURCE_URL
    - jdbc:postgresql://{Hostの値}:{Portの値}/{Databaseの値}
- SPRING_DATASOURCE_USERNAME
    - User の値
- SPRING_DATASOURCE_PASSWORD
    - Password の値
- SPRING_DATASOURCE_DRIVER_CLASS_NAME
    - org.postgresql.Driver

Google の OpenID Connect の設定も行い、環境変数として設定します。

- SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID
    - Gooele の OpenID Connect のクライアントID
- SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET
    - Gooele の OpenID Connect のクライアントシークレット
- 承認済みのリダイレクト URI には `https://{アプリのURL}/login/oauth2/code/google` を指定します。

### 4. Docker image 作成 & publish

```
$ ./gradlew build  // build して jar ファイルを更新します
$ heroku container:push web
$ heroku container:release web
```

### 5. 動作確認

https://{アプリのURL} にアクセスします。

[Heroku上のデモ](https://radiant-brook-88401.herokuapp.com)

![操作イメージ](https://user-images.githubusercontent.com/1412761/61167208-7d64e900-a576-11e9-94e1-19a261ce5f87.gif)
