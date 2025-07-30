package lk.ijse.gdse74.mytest2.responsive.bo;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOTypes boType) {
        return (T) switch (boType) {
            case CUSTOMER -> (T) new CustomerBOImpl();
            case FARMER -> (T) new FarmerBOImpl();
            case SUPPLIER -> (T) new SupplierBOImpl();
            case EMPLOYEE -> (T) new EmployeeBOImpl();
            case RAW_PADDY -> (T) new RawPaddyBOImpl();
            case USER -> (T) new UserBOImpl();
            case MILLING_PROCESS -> (T) new MillingProcessBOImpl();
            case FINISHED_PRODUCT -> (T) new FinishedProductBOImpl(); // New case
        };
    }
}