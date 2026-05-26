package jp.co.metateam.library.model;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalManageDto {

    private Long id;

    @NotBlank(message = "社員番号は必須です")
    private String employeeId;

    @NotNull(message = "貸出予定日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedRentalOn;

    @NotNull(message = "返却予定日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReturnOn;

    @NotBlank(message = "在庫番号は必須です")
    private String stockId;

    @NotNull(message = "ステータスは必須です")
    private Integer status;
}
