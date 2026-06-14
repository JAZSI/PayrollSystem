package com.com253.payrollsystem.statutory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * One row of a contribution table. Field meaning varies by agency:
 * SSS [lower,upper) -> amount; PhilHealth lower=floor, upper=ceiling, rate;
 * Pag-IBIG [lower,upper) -> rate, amount=cap; BIR [lower,upper) -> amount=base, rate.
 */
@Entity
@Table(name = "contribution_brackets")
public class ContributionBracketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(nullable = false)
    private int seq;

    @Column(name = "lower_bound", nullable = false)
    private double lowerBound;

    @Column(name = "upper_bound", nullable = false)
    private double upperBound;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private double rate;

    protected ContributionBracketEntity() {
        // JPA
    }

    public ContributionBracketEntity(Long tableId, int seq, double lowerBound,
                                     double upperBound, double amount, double rate) {
        this.tableId = tableId;
        this.seq = seq;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.amount = amount;
        this.rate = rate;
    }

    public Long getId() { return id; }
    public Long getTableId() { return tableId; }

    public int getSeq() { return seq; }
    public void setSeq(int seq) { this.seq = seq; }

    public double getLowerBound() { return lowerBound; }
    public void setLowerBound(double v) { this.lowerBound = v; }

    public double getUpperBound() { return upperBound; }
    public void setUpperBound(double v) { this.upperBound = v; }

    public double getAmount() { return amount; }
    public void setAmount(double v) { this.amount = v; }

    public double getRate() { return rate; }
    public void setRate(double v) { this.rate = v; }
}
