package com.goorm.friendchise.domain.customer.presentation;


import com.goorm.friendchise.domain.customer.application.CustomerService;
import com.goorm.friendchise.domain.customer.dto.request.*;
import com.goorm.friendchise.domain.customer.dto.response.CustomerDetailResponse;
import com.goorm.friendchise.domain.customer.dto.response.CustomerPersistResponse;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/customer")
@RestController
@RequiredArgsConstructor
public class CustomerController
{
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerPersistResponse> register(@RequestBody @Valid CustomerCreateRequest request) {
        CustomerPersistResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid CustomerLoginRequest request
    )
    {
        TokenResponse response = customerService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<TokenResponse> logout(
            @RequestBody @Valid CustomerDestinationRequest locationRequest) {
        customerService.logout(locationRequest);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/mypage")
    public ResponseEntity<CustomerDetailResponse> myPage() {
        return ResponseEntity.ok(customerService.myPage());
    }


    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request)
    {
        customerService.updatePassword(request.newPassword());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/nearest-store")
    public ResponseEntity<String> findNearestStore(@RequestParam String address, @RequestParam String franchiseName) {
        CustomerRecommendStoreRequest request = new CustomerRecommendStoreRequest(address, franchiseName);
        String storeAddress = customerService.findNearestStoreWithCache(request);
        return ResponseEntity.ok(storeAddress);
    }

    @GetMapping("/nearest-store/no-cache")
    public ResponseEntity<String> findNearestStoreWithNoCache(@RequestParam String address, @RequestParam String franchiseName) {
        CustomerRecommendStoreRequest request = new CustomerRecommendStoreRequest(address, franchiseName);
        String storeAddress = customerService.findNearestStoreWithNoCache(request);
        return ResponseEntity.ok(storeAddress);
    }
}
