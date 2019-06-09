package net.jp.vss.sample.controller.tasks

import net.jp.vss.sample.controller.exceptions.HttpNotFoundException
import net.jp.vss.sample.domain.exceptions.NotFoundException
import net.jp.vss.sample.usecase.tasks.GetTaskUseCase
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.validation.constraints.Pattern

/**
 * GetTask の APIController.
 *
 * @property getTaskUseCase GetTask の UseCase
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
class GetTaskApiController(
    private val getTaskUseCase: GetTaskUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(GetTaskApiController::class.java)
    }

    /**
     * GetTask.
     *
     * @param taskCode パラメータ
     * @return レスポンス
     */
    @GetMapping(value = ["/{task_code}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getTask(
        @PathVariable("task_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        taskCode: String
    ): ResponseEntity<TaskUseCaseResult> {

        try {
            return ResponseEntity.ok(getTaskUseCase.getTask(taskCode))
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }

    /**
     * GetNewTask.
     *
     * Task 登録用の情報を取得します
     * @return レスポンス
     */
    @GetMapping(value = ["/_new"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getNewTask(): ResponseEntity<CreateTaskApiParameter> {
        return ResponseEntity.ok(CreateTaskApiParameter.newTask())
    }
}
