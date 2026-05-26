package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RentalManage")
public class RentalManage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private String stock_id;

    @Column(name = "employee_id", nullable = false)
    private String employee_id;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "expected_rental_on", nullable = false)
    private LocalDate expected_rental_on;

    @Column(name = "expected_return_on", nullable = false)
    private LocalDate expected_return_on;

    @Column(name = "rentaled_at")
    private Timestamp rentaled_at;

    @Column(name = "returned_at")
    private Timestamp returned_at;

    @Column(name = "canceled_at")
    private Timestamp canceled_at;

    public Long getid() {
        return this.id;
    }

    public String getstock_id() {
        return this.stock_id;
    }

    public String getemployee_id() {
        return this.employee_id;
    }

    public Integer getstatus() {
        return this.status;
    }

    public LocalDate getexpected_rental_on() {
        return this.expected_rental_on;
    }

    public LocalDate getexpected_return_on() {
        return this.expected_return_on;
    }

    public Timestamp getrentaled_at() {
        return this.rentaled_at;
    }

    public Timestamp getreturned_at() {
        return this.returned_at;
    }

    public Timestamp getcanceled_at() {
        return this.canceled_at;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public void setstock_id(String stock_id) {
        this.stock_id = stock_id;
    }

    public void setemployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setstatus(Integer status) {
        this.status = status;
    }

    public void setexpected_rental_on(LocalDate expected_rental_on) {
        this.expected_rental_on = expected_rental_on;
    }

    public void setexpected_return_on(LocalDate expected_return_on) {
        this.expected_return_on = expected_return_on;
    }

    public void setrentaled_at(Timestamp rentaled_at) {
        this.rentaled_at = rentaled_at;
    }

    public void setreturned_at(Timestamp returned_at) {
        this.returned_at = returned_at;
    }

    public void setcanceled_at(Timestamp canceled_at) {
        this.canceled_at = canceled_at;
    }

}
