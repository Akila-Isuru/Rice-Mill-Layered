package lk.ijse.gdse74.mytest2.responsive.dao;

//import lk.ijse.gdse74.mytest2.responsive.bo.custom.impl.MillingProcessBOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.EmployeeDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.FarmerDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.RawPaddyDAOImpl; // New import
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.SupplierDAOImpl;

//import static lk.ijse.gdse74.mytest2.responsive.bo.BOTypes.MILLING_PROCESS;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return switch (daoType) {
            case CUSTOMER -> (T) new CustomerDAOImpl();
            case FARMER -> (T) new FarmerDAOImpl();
            case SUPPLIER -> (T) new SupplierDAOImpl();
            case EMPLOYEE -> (T) new EmployeeDAOImpl();
            case RAW_PADDY -> (T) new RawPaddyDAOImpl();
          //  case MILLING_PROCESS -> (T)new MillingProcessBOImpl();// New case
// New case
        };
    }
}