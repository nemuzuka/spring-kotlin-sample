# Spring + Kotlin Sample
Spring と Kotlin を組み合わせたサンプル

## Repository のテスト

[FlywayTest](https://github.com/flyway/flyway-test-extensions) を使用してます。

* [JdbcTaskRepositryTest](src/test/kotlin/net/jp/vss/sample/infrastructure/tasks/JdbcTaskRepositryTest.kt)
    * 実際に RDBMS(H2) へアクセスしてテストします

## Service のテスト

[Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) を使用してます。

* [CreateTaskUseCaseImplTest](src/test/kotlin/net/jp/vss/sample/usecase/tasks/CreateTaskUseCaseImplTest.kt)
    * Repositry の呼び出しがありますが、Mock を使用するので RDBMS へアクセスしません

## Controller のテスト

* [CreateTaskApiControllerTest](src/test/kotlin/net/jp/vss/sample/controller/tasks/CreateTaskApiControllerTest.kt)
    * UseCase の呼び出しがありますが、Mocl を使用するので RDBMS へアクセスしません
    * テストできるのはメソッド呼び出し以降です。`@Valid` の validate 等はこのテストではできません

## Integration のテスト
* [CreateTaskApiIntegrationTest](src/test/kotlin/net/jp/vss/sample/controller/tasks/CreateTaskApiIntegrationTest.kt)
    * サーバを立ち上げ、本物の Bean を Injection して実行します


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
$ curl -H 'Content-Type:application/json' http://localhost:8089/api/tasks/HOGE_001 | jq "."
```

```json
{
  "timestamp": "2019-04-30T00:18:56.040+0000",
  "status": 404,
  "error": "Not Found",
  "message": "Task(HOGE_001) は存在しません",
  "path": "/api/tasks/HOGE_001"
}
```

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
