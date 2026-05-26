package jp.co.metateam.library.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.server.ServerSecurityMarker;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.values.RentalStatus;

@Service
public class RentalManageService {

    private final RentalManageRepository rentalManageRepository;

    @Autowired
    public RentalManageService(RentalManageRepository rentalManageRepository) {
        this.rentalManageRepository = rentalManageRepository;
    }
    public String validateStatus(RentalManageDto rentalManageDto) {
        // 必要な値が null の場合は、DTO側の必須チェックに任せる
        if (rentalManageDto.getStockId() == null
                || rentalManageDto.getExpectedRentalOn() == null
                || rentalManageDto.getExpectedReturnOn() == null
                || rentalManageDto.getStatus() == null) {
            return null;
        }
        // --- 以下日付・ステータスチェック ---
        LocalDate today = LocalDate.now();
        LocalDate rentalDate = rentalManageDto.getExpectedRentalOn();
        LocalDate returnDate = rentalManageDto.getExpectedReturnOn();
        Integer status = rentalManageDto.getStatus();
        // 返却済み
        if (RentalStatus.RETURNED.getValue().equals(status)) {
            return "返却済みは選択できません";
        }
        // キャンセル
        if (RentalStatus.CANCELED.getValue().equals(status)) {
            return "キャンセルは選択できません";
        }
        // 貸出予定日が本日なのに、ステータスが「貸出中」以外
        if (rentalDate.isEqual(today) && !RentalStatus.RENTAlING.getValue().equals(status)) {
            return "貸出予定日が本日の場合、ステータスは「貸出中」を選択してください";
        }
        // 貸出予定日が未来日付なのに、ステータスが「貸出中」になっている
        if (rentalDate.isAfter(today) && RentalStatus.RENTAlING.getValue().equals(status)) {
            return "貸出予定日が未来の日付の場合、ステータスに「貸出中」は選択できません";
        }
        // 返却予定日が貸出予定日と同じ、またはそれより前
        if (returnDate.isBefore(rentalDate) || returnDate.isEqual(rentalDate)) {
            return "返却予定日は、貸出予定日より後の日付を設定してください";
        }
        return null;
    }

    @Transactional
    public void save(RentalManageDto rentalManageDto) {
        RentalManage rental = new RentalManage();

        rental.setid(rentalManageDto.getId());
        rental.setstock_id(rentalManageDto.getStockId());
        rental.setemployee_id(rentalManageDto.getEmployeeId());
        rental.setstatus(rentalManageDto.getStatus());
        rental.setexpected_rental_on(rentalManageDto.getExpectedRentalOn());
        rental.setexpected_return_on(rentalManageDto.getExpectedReturnOn());
        rental.setrentaled_at(Timestamp.valueOf(LocalDateTime.now()));
        rental.setreturned_at(Timestamp.valueOf(LocalDateTime.now()));
        rental.setcanceled_at(Timestamp.valueOf(LocalDateTime.now()));

        // 貸出開始なので、貸出日時だけを現在時刻
        rental.setrentaled_at(Timestamp.valueOf(LocalDateTime.now()));
        // 返却やキャンセルはまだ行われていないため、通常はnullにする（または未設定）
        rental.setreturned_at(null);
        rental.setcanceled_at(null);
        
        this.rentalManageRepository.save(rental);
    }
}
