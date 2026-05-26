package jp.co.metateam.library.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.service.StockService;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.values.RentalStatus;
import lombok.extern.log4j.Log4j2;

/**
 * 貸出管理関連クラス
 */
@Log4j2
@Controller
public class RentalManageController {

    private final RentalManageService rentalManageService;
    private final StockService stockService;
    private final AccountService accountService;

    @Autowired
    public RentalManageController(StockService stockService, AccountService accountService,
            RentalManageService rentalManageService) {
        this.stockService = stockService;
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
    }

    /**
     * 貸出一覧画面初期表示
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        return "/rental/index";
    }

    @GetMapping("/rental/add")
    public String add(Model model) {
        model.addAttribute("rentalManageDto", new RentalManageDto());

        List<Account> accounts = this.accountService.findAll();
        List<Stock> stockList = this.stockService.findAll();

        model.addAttribute("accounts", accounts);
        model.addAttribute("stockList", stockList);
        model.addAttribute("rentalStatus", RentalStatus.values());

        return "/rental/add";
    }

    @PostMapping("/rental/add")
    public String save(
            @Valid @ModelAttribute RentalManageDto rentalManageDto,
            BindingResult result,
            Model model) {

        // 今までのチェック（未入力や、日付の矛盾、在庫ステータスチェックなど）
        String statusError = rentalManageService.validateStatus(rentalManageDto);
        if (statusError != null) {
            if (statusError.startsWith("選択された在庫") || statusError.startsWith("指定された在庫")) {
                if (!result.hasFieldErrors("stockId")) {
                    result.rejectValue("stockId", "error.stockId", statusError);
                }
            } else if (statusError.startsWith("返却予定日")) {
                if (!result.hasFieldErrors("expectedRentalOn")) {
                    result.rejectValue("expectedRentalOn", "error.expectedRentalOn", statusError);
                }
            } else {
                if (!result.hasFieldErrors("status")) {
                    result.rejectValue("status", "error.status", statusError);
                }
            }
        }

        // ここまでのエラーが「1つもなかった場合だけ」スケジュール重複チェック
        if (!result.hasErrors()) {

            String duplicateError = rentalManageService.validateScheduleDuplicate(rentalManageDto);

            if (duplicateError != null) {

                result.rejectValue("stockId", "error.stockId", duplicateError);
            }
        }

        if (result.hasErrors()) {
            refillModelData(model);
            return "/rental/add";
        }

        this.rentalManageService.save(rentalManageDto);
        return "redirect:/rental/index";
    }

    private void refillModelData(Model model) {
        model.addAttribute("accounts", this.accountService.findAll());
        model.addAttribute("stockList", this.stockService.findAll());
        model.addAttribute("rentalStatus", RentalStatus.values());
    }
}
