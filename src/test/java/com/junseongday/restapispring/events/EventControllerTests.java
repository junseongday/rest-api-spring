package com.junseongday.restapispring.events;

import com.junseongday.restapispring.accounts.Account;
import com.junseongday.restapispring.accounts.AccountRepository;
import com.junseongday.restapispring.accounts.AccountRole;
import com.junseongday.restapispring.accounts.AccountService;
import com.junseongday.restapispring.common.AppProperties;
import com.junseongday.restapispring.common.BaseControllerTest;
import com.junseongday.restapispring.common.TestDescription;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Before
    public void dbClear(){
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @TestDescription("정상적으로 이벵트를 생성하는 태스트")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 13, 13, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 14, 13, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 4, 15, 13, 30))
                .endEventDateTime(LocalDateTime.of(2020, 4, 16, 13, 30))
                .basePrice(100)
                .maxPrice(200)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(not(EventStatus.PUBLISHED)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description if new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of bigin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit Of Enrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("Identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description if new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of bigin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit Of Enrollment of new event"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this event is offline meeting or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile an existing event")
                        )

                ))
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 태스트")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 13, 13, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 14, 13, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 4, 15, 13, 30))
                .endEventDateTime(LocalDateTime.of(2020, 4, 16, 13, 30))
                .basePrice(100)
                .maxPrice(200)
                .location("강남역 D2 스타텁 팩토리")
                .id(100)
                .free(true)
                .offline(true)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 13, 13, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 14, 13, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 4, 12, 13, 30))
                .endEventDateTime(LocalDateTime.of(2020, 4, 11, 13, 30))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        //when
        mockMvc.perform(get("/api/events")
                .param("paga", "1")
                .param("size", "10")
                .param("sort", "id,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-events"))
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        //when
        mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .param("paga", "1")
                .param("size", "10")
                .param("sort", "id,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("get-events"))
        ;
    }
    private Event generateEvent(int i, Account account) {
        Event event = buildEvent(i);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event generateEvent(int i) {
        Event event = buildEvent(i);
        Event save = this.eventRepository.save(event);
        return save;
    }

    private Event buildEvent(int i) {
        return Event.builder()
                    .name("event " + i)
                    .description("test event")
                    .beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 13, 13, 30))
                    .closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 14, 13, 30))
                    .beginEventDateTime(LocalDateTime.of(2020, 4, 15, 13, 30))
                    .endEventDateTime(LocalDateTime.of(2020, 4, 16, 13, 30))
                    .basePrice(100)
                    .maxPrice(200)
                    .location("강남역 D2 스타텁 팩토리")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();
    }

    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    public void queryEvent() throws Exception {
        // Given
        Account account = createAccount();
        Event event = this.generateEvent(100, account);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을때 404 응답받기")
    public void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Account account = createAccount();

        Event event = generateEvent(200, account);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("입력값이 없 경우에 이벤트 수정 실패")
    public void updateEvent400Empty() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = new EventDto();
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400wrong() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트를 수정 싶패")
    public void updateEvent404() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = modelMapper.map(event, EventDto.class);


        // When & Then
        mockMvc.perform(put("/api/events/1234125")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Account createAccount() {
        Account junseong = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN))
                .build();
        accountService.saveAccount(junseong);
        return junseong;
    }
    @NotNull
    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer "+ getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
      if(needToCreateAccount) {
          createAccount();
      }

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        var resultString = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(resultString).get("access_token").toString();
    }
}
