package jp.co.metateam.library.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final StockRepository stockRepository;

    @Autowired
    public RentalManageService(RentalManageRepository rentalManageRepository, StockRepository stockRepository) {
        this.rentalManageRepository = rentalManageRepository;
        this.stockRepository = stockRepository;
    }

    public String validateStatus(RentalManageDto rentalManageDto) {
        // 必要な値が null の場合は、DTO側の必須チェックに任せる
        if (rentalManageDto.getStockId() == null
                || rentalManageDto.getExpectedRentalOn() == null
                || rentalManageDto.getExpectedReturnOn() == null
                || rentalManageDto.getStatus() == null) {
            return null;
        }
        // 在庫の利用可否チェック
        Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
        if (stock != null) {
            if (Integer.valueOf(1).equals(stock.getStatus())) {
                return "選択された在庫は現在利用不可のため、貸出登録できません";
            }
        } else {
            return "指定された在庫管理番号が存在しません";
        }
        // --- 以下日付・ステータスチェック ---
        LocalDate today = LocalDate.now();
        LocalDate rentalDate = rentalManageDto.getExpectedRentalOn();
        LocalDate returnDate = rentalManageDto.getExpectedReturnOn();
        Integer status = rentalManageDto.getStatus();

        if (RentalStatus.RETURNED.getValue().equals(status)) {
            return "返却済みは選択できません";
        }
        if (RentalStatus.CANCELED.getValue().equals(status)) {
            return "キャンセルは選択できません";
        }
        if (rentalDate.isEqual(today) && !RentalStatus.RENTAlING.getValue().equals(status)) {
            return "貸出予定日が本日の場合、ステータスは「貸出中」を選択してください";
        }
        if (rentalDate.isAfter(today) && RentalStatus.RENTAlING.getValue().equals(status)) {
            return "貸出予定日が未来の日付の場合、ステータスに「貸出中」は選択できません";
        }
        if (returnDate.isBefore(rentalDate) || returnDate.isEqual(rentalDate)) {
            return "返却予定日は、貸出予定日より後の日付を設定してください";
        }
        return null;
    }

    // スケジュール重複チェック
    public String validateScheduleDuplicate(RentalManageDto rentalManageDto) {
        // テーブルから全件取得
        List<RentalManage> allRentals = this.rentalManageRepository.findAll();
        // 画面から入力された新データの日付
        LocalDate newRentalOn = rentalManageDto.getExpectedRentalOn();
        LocalDate newReturnOn = rentalManageDto.getExpectedReturnOn();
        // ループ機能で1件ずつ比較
        for (RentalManage existing : allRentals) {
            if (rentalManageDto.getId() != null && rentalManageDto.getId().equals(existing.getid())) {
                continue;
            }
            // 同じ在庫管理番号か
            boolean isSameStock = rentalManageDto.getStockId().equals(existing.getstock_id());
            // 既存データのステータスが「貸出待ち」または「貸出中」か
            Integer status = existing.getstatus();
            boolean isReservedOrRenting = (status != null && (status == 1 || status == 2));
            // 同じ在庫で、かつ予約中・貸出中のデータ
            if (isSameStock && isReservedOrRenting) {
                LocalDate existRentalOn = existing.getexpected_rental_on();
                LocalDate existReturnOn = existing.getexpected_return_on();
                if (existRentalOn != null && existReturnOn != null) {
                    // 重なっていない条件を定義
                    boolean isSafeAfter = newRentalOn.isAfter(existReturnOn) || newRentalOn.isEqual(existReturnOn);
                    boolean isSafeBefore = newReturnOn.isBefore(existRentalOn) || newReturnOn.isEqual(existRentalOn);
                    // セーフじゃない場合はエラー文を返却
                    if (!(isSafeAfter || isSafeBefore)) {
                        return "選択された在庫は、指定された期間（"
                                + existRentalOn + " ～ " + existReturnOn
                                + "）にすでに貸出中または貸出待ちの予約があるため登録できません";
                    }
                }
            }
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
        rental.setreturned_at(null);
        rental.setcanceled_at(null);

        this.rentalManageRepository.save(rental);
    }
}