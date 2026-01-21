package com.example.nhatro.controller.ServiceControllers;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.config.IsAdmin;
import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceRequestDto;
import com.example.nhatro.dto.response.ServiceResponseDto;
import com.example.nhatro.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Admin thêm dịch vụ mới
     */
    @PostMapping
    @IsAdmin
    public ApiResponse<ServiceResponseDto> addService(@Valid @RequestBody ServiceRequestDto request) {
        try {
            ServiceResponseDto response = serviceService.addService(request);
            return ApiResponse.<ServiceResponseDto>builder()
                    .code(HttpStatus.CREATED.value())
                    .message("Thêm dịch vụ thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Thêm dịch vụ thất bại: " + e.getMessage());
        }
    }

    
    /**
     * Admin lấy tất cả dịch vụ
     */
    @GetMapping
    @IsAdmin
    public ApiResponse<List<ServiceResponseDto>> getAllServices() {
        List<ServiceResponseDto> services = serviceService.getAllServices();
        return ApiResponse.<List<ServiceResponseDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách dịch vụ thành công")
                .result(services)
                .build();
    }

    

    /**
     * Admin cập nhật dịch vụ
     */
    @PutMapping("/{serviceId}")
    @IsAdmin
    public ApiResponse<ServiceResponseDto> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequestDto request) {
        try {
            ServiceResponseDto response = serviceService.updateService(serviceId, request);
            return ApiResponse.<ServiceResponseDto>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật dịch vụ thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Cập nhật dịch vụ thất bại: " + e.getMessage());
        }
    }

    /**
     * Admin xóa dịch vụ
     */
    @DeleteMapping("/{serviceId}")
    @IsAdmin
    public ApiResponse<Void> deleteService(@PathVariable Long serviceId) {
        try {
            serviceService.deleteService(serviceId);
            return ApiResponse.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa dịch vụ thành công")
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Xóa dịch vụ thất bại: " + e.getMessage());
        }
    }

    /**
     * Owner thêm dịch vụ vào hostel của mình
     */
    @PostMapping("/owner")
    @IsOwner
    public ApiResponse<ServiceResponseDto> addServiceByOwner(
            @Valid @RequestBody ServiceRequestDto request,
            Principal principal) {
        try {
            ServiceResponseDto response = serviceService.addServiceByOwner(request, principal.getName());
            return ApiResponse.<ServiceResponseDto>builder()
                    .code(HttpStatus.CREATED.value())
                    .message("Thêm dịch vụ vào hostel của bạn thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Thêm dịch vụ thất bại: " + e.getMessage());
        }
    }
    /**
     * Owner lấy tất cả dịch vụ của mình
     */
    @GetMapping("/my-services")
    @IsOwner
    public ApiResponse<List<ServiceResponseDto>> getMyServices(Principal principal) {
        List<ServiceResponseDto> services = serviceService.getServicesForCurrentOwner(principal.getName());
        return ApiResponse.<List<ServiceResponseDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách dịch vụ của bạn thành công")
                .result(services)
                .build();
    }

    /**
     * Owner cập nhật dịch vụ của mình (tự động check ownership)
     */
    @PutMapping("/owner/{serviceId}")
    @IsOwner
    public ApiResponse<ServiceResponseDto> updateServiceByOwner(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequestDto request,
            Principal principal) {
        try {
            ServiceResponseDto response = serviceService.updateServiceByOwner(serviceId, request, principal.getName());
            return ApiResponse.<ServiceResponseDto>builder()
                    .code(HttpStatus.OK.value())
                    .message("Cập nhật dịch vụ thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Cập nhật dịch vụ thất bại: " + e.getMessage());
        }
    }

    /**
     * Owner xóa dịch vụ của mình (tự động check ownership)
     */

    @DeleteMapping("/owner/{serviceId}")
    @IsOwner
    public ApiResponse<Void> deleteServiceByOwner(@PathVariable Long serviceId, Principal principal) {
        try {
            serviceService.deleteServiceByOwner(serviceId, principal.getName());
            return ApiResponse.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa dịch vụ thành công")
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Xóa dịch vụ thất bại: " + e.getMessage());
        }
    }
}
