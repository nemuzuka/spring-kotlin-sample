package net.jp.vss.sample.controller.tasks

import net.jp.vss.sample.controller.exceptions.HttpConflictException
import net.jp.vss.sample.controller.exceptions.HttpNotFoundException
import net.jp.vss.sample.domain.exceptions.NotFoundException
import net.jp.vss.sample.domain.exceptions.UnmatchVersionException
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import net.jp.vss.sample.usecase.tasks.UpdateTaskUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/**
 * UpdateTask の APIController.
 *
 * @property updateTaskUseCase UpdateTask の UseCase
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
class UpdateTaskApiController(
    private val updateTaskUseCase: UpdateTaskUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateTaskApiController::class.java)
    }

    /**
     * UpdateTask.
     *
     * @param taskCode タスクコード
     * @param parameter パラメータ
     * @param version 排他制御用 version
     * @return レスポンス
     */
    @PostMapping(value = ["/{task_code}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTask(
        @NotNull
        @PathVariable("task_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        taskCode: String,

        @Validated
        @RequestBody
        parameter: UpdateTaskApiParameter,

        @RequestParam("version")
        version: Long?
    ): ResponseEntity<TaskUseCaseResult> {

        try {
            // 本当はログインユーザの情報から取ってきたい気持ちがあるけど固定値で
            val result = updateTaskUseCase.updateTask(
                parameter.toParameter(taskCode, version, "DUMMY_UPDATE_USER_CODE"))
            return ResponseEntity.ok(result)
        } catch (e: UnmatchVersionException) {
            // version 相違
            log.info("Invalid Task({}) version", taskCode)
            throw HttpConflictException(e.message!!, e)
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }
}
