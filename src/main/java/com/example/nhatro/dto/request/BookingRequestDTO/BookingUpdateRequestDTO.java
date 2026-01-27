package com.example.nhatro.dto.request.BookingRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingUpdateRequestDTO {
    private Long bookingId;
    private String status; // NEW STATUS
}
