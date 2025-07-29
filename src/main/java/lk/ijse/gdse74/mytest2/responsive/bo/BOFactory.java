package lk.ijse.gdse74.mytest2.responsive.bo;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.CustomerBOImpl;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.FarmerBOImpl;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.MachineMaintenanceBOImpl;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.MillingProcessBOImpl;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.PaddyBOImpl; // Keep if you have this class
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.RawPaddyBOImpl;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.SupplierBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOTypes boType) {
        return switch (boType) {
            case CUSTOMER -> (T) new CustomerBOImpl();
            case FARMER -> (T) new FarmerBOImpl();
           // case MACHINE_MAINTENANCE -> (T) new MachineMaintenanceBOImpl();
          //  case MILLING_PROCESS -> (T) new MillingProcessBOImpl();
          //  case PADDY -> (T) new PaddyBOImpl();
          //  case RAW_PADDY -> (T) new RawPaddyBOImpl();
            case SUPPLIER -> (T) new SupplierBOImpl();
        };
    }
}