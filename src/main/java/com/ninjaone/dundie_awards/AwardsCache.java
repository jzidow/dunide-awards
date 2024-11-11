package com.ninjaone.dundie_awards;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AwardsCache {
    private final static AtomicInteger totalAwards = new AtomicInteger(0);

    public void setTotalAwards(int awards) {
        AwardsCache.totalAwards.set(awards);
    }

    public int getTotalAwards(){
        return totalAwards.get();
    }

    public void addAwards(int awards){
        totalAwards.addAndGet(awards);
    }

    public void subtractAwards(int awards){
        totalAwards.addAndGet(-awards);
    }
}
