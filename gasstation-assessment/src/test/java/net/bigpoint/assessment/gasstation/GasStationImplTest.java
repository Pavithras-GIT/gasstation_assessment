package net.bigpoint.assessment.gasstation;

import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GasStationImplTest {
    private GasStationImpl gasStation;
    @BeforeEach
    public void setup(){
        gasStation = new GasStationImpl();
        GasPump regular = new GasPump(GasType.REGULAR, 500);
        GasPump diesel = new GasPump(GasType.DIESEL, 600);
        GasPump superGas = new GasPump(GasType.SUPER, 100);
        gasStation.addGasPump(regular);
        gasStation.addGasPump(diesel);
        gasStation.addGasPump(superGas);
        gasStation.setPrice(GasType.REGULAR, 1);
        gasStation.setPrice(GasType.DIESEL, 1.5);
        gasStation.setPrice(GasType.SUPER, 2.3);
    }

    @Test
    void getGasPumpsTest(){
        assertNotNull(gasStation.getGasPumps());
        assertEquals(3, gasStation.getGasPumps().size());
    }

    @Test
     void setGasPumpsTest(){
        GasPump dieselPumpNew = new GasPump(GasType.DIESEL, 600);
        gasStation.addGasPump(dieselPumpNew);
        assertEquals(4, gasStation.getGasPumps().size());
    }

    @Test
    void buyGasTest() throws GasTooExpensiveException, NotEnoughGasException {
        double amountInLitres = 50;
        double price = gasStation.getPrice(GasType.SUPER) * amountInLitres;
        assertEquals(gasStation.buyGas(GasType.SUPER,amountInLitres, 2.5),price);
    }

    @Test
    void buyGasTestTooExpensiveTest() {
        double amountInLitres = 50;
        Assertions.assertThrows(GasTooExpensiveException.class, () -> gasStation.buyGas(GasType.SUPER,amountInLitres, 2));
    }

    @Test
    void buyGasNotEnoughTest() {
        double amountInLitres = 250;
        Assertions.assertThrows(NotEnoughGasException.class, () -> gasStation.buyGas(GasType.SUPER,amountInLitres, 2.7));
    }

    @Test
    void priceTest(){
        gasStation.setPrice(GasType.DIESEL, 2);
        assertEquals(2,gasStation.getPrice(GasType.DIESEL));
    }

}
