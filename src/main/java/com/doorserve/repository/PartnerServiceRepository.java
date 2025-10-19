package com.doorserve.repository;

import com.doorserve.model.PartnerService;
import com.doorserve.model.ServicesCatalog;
import com.doorserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerServiceRepository extends JpaRepository<PartnerService, Long> {
    List<PartnerService> findByPartner(User partner);
    List<PartnerService> findByServiceCatalog(ServicesCatalog serviceCatalog);
    List<PartnerService> findByServiceCatalogAndAvailableTrue(ServicesCatalog serviceCatalog);
    Optional<PartnerService> findByPartnerAndServiceCatalog(User partner, ServicesCatalog serviceCatalog);
    
    @Query("SELECT ps FROM PartnerService ps WHERE ps.serviceCatalog.id = :serviceId AND ps.available = true ORDER BY ps.rating DESC, ps.price ASC")
    List<PartnerService> findAvailablePartnersByServiceId(@Param("serviceId") Long serviceId);
    
    @Query("SELECT ps FROM PartnerService ps WHERE ps.partner.id = :partnerId")
    List<PartnerService> findByPartnerId(@Param("partnerId") Long partnerId);
    
    @Query("SELECT ps FROM PartnerService ps WHERE ps.partner.id = :partnerId AND ps.serviceCatalog.id = :serviceCatalogId")
    Optional<PartnerService> findByPartnerIdAndServiceCatalogId(@Param("partnerId") Long partnerId, @Param("serviceCatalogId") Long serviceCatalogId);
}