package kim.seokwon.web.sample.meetingroombooking.controller;

import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

class MeetingRoomControllerTest {

    @InjectMocks
    private MeetingRoomController   meetingRoomController;

    private MockMvc mockMvc;

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(meetingRoomController).build();
    }

    @Test
    void createRecord() {
        Assertions.assertDoesNotThrow(() -> {
            this.mockMvc.perform(
                    post("/api/resources/meeting-room/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("test")
            ).andExpect(status().is2xxSuccessful());
        });
    }

    @Test
    void modifyRecord() {
    }

    @Test
    void getDailyBookingInfoByDay() {
    }

    @Test
    void getDailyBookingInfoByRange() {
    }

    @Test
    void getBookingInfoByDay() {
    }

    @Test
    void removeBookingInfo() {
    }

    @Test
    void getBookingParam() {
    }

    @Test
    void getMeetingRoomList() {
    }

    @Test
    void getTimeTable() {
    }
}