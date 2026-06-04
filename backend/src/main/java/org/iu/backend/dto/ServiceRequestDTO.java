package org.iu.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceRequestDTO {
    private Long id;

    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    @NotNull(message = "Status is required")
    private String status;

    @NotNull(message = "Requester ID is required")
    private Long requesterId;
    private String requesterUsername;

    @NotNull(message = "Service Offer ID is required")
    private Long serviceOfferId;
    private String serviceOfferTitle;

    public ServiceRequestDTO() {
    }

    public ServiceRequestDTO(Long id, String message, String status, Long requesterId, 
                            String requesterUsername, Long serviceOfferId, String serviceOfferTitle) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.requesterId = requesterId;
        this.requesterUsername = requesterUsername;
        this.serviceOfferId = serviceOfferId;
        this.serviceOfferTitle = serviceOfferTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public Long getServiceOfferId() {
        return serviceOfferId;
    }

    public void setServiceOfferId(Long serviceOfferId) {
        this.serviceOfferId = serviceOfferId;
    }

    public String getServiceOfferTitle() {
        return serviceOfferTitle;
    }

    public void setServiceOfferTitle(String serviceOfferTitle) {
        this.serviceOfferTitle = serviceOfferTitle;
    }
}
