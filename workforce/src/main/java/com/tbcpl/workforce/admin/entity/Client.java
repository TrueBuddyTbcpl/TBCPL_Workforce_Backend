package com.tbcpl.workforce.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @NotBlank(message = "client name is required")
    @Size(max = 255, message = "Client name cannot exceed 255 character")
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Lob
    @Column(name = "client_logo", columnDefinition = "LONGBLOB")
    private byte[] clientLogo;

    @Column(name = "logo_file_name")
    private String logoFileName;

    @Column(name = "logo_content_type")
    private String logoContentType;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientProduct> clientProducts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    public void addClientProduct(ClientProduct product) {
        clientProducts.add(product);
        product.setClient(null);
    }

    public void removeProduct(ClientProduct product) {
        clientProducts.remove(product);
        product.setClient(null);
    }
}
