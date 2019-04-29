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
