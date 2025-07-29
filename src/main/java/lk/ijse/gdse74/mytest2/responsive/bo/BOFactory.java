package lk.ijse.gdse74.mytest2.responsive.bo;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.CustomerBOImpl;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.EmployeeBOImpl; // New import
import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.FarmerBOImpl;
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
            case SUPPLIER -> (T) new SupplierBOImpl();
            case EMPLOYEE -> (T) new EmployeeBOImpl(); // New case
        };
    }
}