package com.junseongday.restapispring.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();

    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String name = "Event";
        String spring = "Spring";

        event.setName(name);
        event.setDescription(spring);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);
    }

    @Test
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        event.update();
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false},
        };
    };

    @Test
    @Parameters
    public void testOffline(String location, boolean isLocation) {
        Event event = Event.builder()
                .location(location)
                .build();
        event.update();

        assertThat(event.isOffline()).isEqualTo(isLocation);
    }

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"강남역 네이버 D2 스타텁 팩토리", true},
                new Object[] {null, false},
                new Object[] {"           ", false},
        };
    };
}