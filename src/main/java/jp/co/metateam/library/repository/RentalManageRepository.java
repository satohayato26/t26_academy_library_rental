package jp.co.metateam.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.Stock;

@Repository
public interface RentalManageRepository
        extends JpaRepository<RentalManage, Long> {
    List<RentalManage> findAll();
}
