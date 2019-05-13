package net.jp.vss.sample.controller.users

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.usecase.users.GetUserUseCase
import net.jp.vss.sample.usecase.users.UserUseCaseResultFixtures
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * MeApiController のテスト.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class MeApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getUserUseCase: GetUserUseCase

    @Mock
    private lateinit var oAuth2AuthenticationToken: OAuth2AuthenticationToken

    @Mock
    private lateinit var principal: OAuth2User

    private lateinit var beforeAuthentication: Authentication

    @Before
    fun setUp() {
        beforeAuthentication = SecurityContextHolder.getContext().authentication
        SecurityContextHolder.getContext().authentication = oAuth2AuthenticationToken
    }

    @After
    fun tearDown() {
        SecurityContextHolder.getContext().authentication = beforeAuthentication
    }

    @Test
    @WithMockUser
    fun testMe_NotExistsUser() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        whenever(getUserUseCase.getUser(any(), any())).thenReturn(null)

        // execution
        mockMvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("user_code").value(`is`("")))
            .andExpect(jsonPath("user_name").value(`is`("")))

        verify(getUserUseCase).getUser(authorizedClientRegistrationId, principalName)
    }

    @Test
    @WithMockUser
    fun testMe_ExistsUser() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(any(), any())).thenReturn(user)

        // execution
        mockMvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("user_code").value(`is`(user.userCode)))
            .andExpect(jsonPath("user_name").value(`is`(user.userName)))
    }
}
