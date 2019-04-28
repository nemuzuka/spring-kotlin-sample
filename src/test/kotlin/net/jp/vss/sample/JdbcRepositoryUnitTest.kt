package net.jp.vss.sample

import org.springframework.test.annotation.Rollback
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional

import org.flywaydb.test.FlywayTestExecutionListener
import org.springframework.boot.test.context.SpringBootTest

/**
 * JDBC を使用した Repositry のテストクラスを意味する annotation
 */
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    TransactionalTestExecutionListener::class,
    FlywayTestExecutionListener::class
)
@TestPropertySource("/application-unittest.properties")
@Transactional
@Rollback
@SpringBootTest
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdbcRepositoryUnitTest
