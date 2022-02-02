package net.bigpoint.assessment.gasstation;

import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GasStationImpl implements GasStation {

    private final CopyOnWriteArrayList<GasPump> pumpList = new CopyOnWriteArrayList<>();
    private final AtomicLong  revenue = new AtomicLong(0);
    private final Map<GasType,Double> priceMap = new HashMap<>();
    private final AtomicInteger numberOfSales = new AtomicInteger(0);
    private final AtomicInteger expensiveCancellation = new AtomicInteger(0);
    private final AtomicInteger notEnoughGasCancellation = new AtomicInteger(0);

    @Override
    public void addGasPump(GasPump pump) {
        this.pumpList.add(pump);
    }

    @Override
    public Collection<GasPump> getGasPumps() {
        return this.pumpList;
    }

    @Override
    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
        double priceOfGas = getPrice(type);
        double amountToPay =0;
        if(priceOfGas>maxPricePerLiter){
            expensiveCancellation.incrementAndGet();
            throw new GasTooExpensiveException();
        }
        for(GasPump pump: pumpList){
            if(pump.getGasType().equals(type)){
                if(pump.getRemainingAmount() < amountInLiters){
                    notEnoughGasCancellation.incrementAndGet();
                    throw new NotEnoughGasException();
                }
                else {
                    synchronized (pump) {
                        pump.pumpGas(amountInLiters);
                        amountToPay = priceOfGas * amountInLiters;
                        numberOfSales.incrementAndGet();
                        revenue.addAndGet((long) amountToPay);
                    }
                }
            }
        }
        return amountToPay;
    }

    @Override
    public double getRevenue() {
        return revenue.get();
    }

    @Override
    public int getNumberOfSales() {
        return numberOfSales.get();
    }

    @Override
    public int getNumberOfCancellationsNoGas() {
        return notEnoughGasCancellation.get();
    }

    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return expensiveCancellation.get();
    }

    @Override
    public double getPrice(GasType type) {
        return priceMap.get(type);
    }

    @Override
    public void setPrice(GasType type, double price) {
        priceMap.put(type, price);
    }
}
