package lk.ijse.gdse74.mytest2.responsive.dao;

import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.*;

import static lk.ijse.gdse74.mytest2.responsive.bo.BOTypes.INVENTORY;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return (T) switch (daoType) {
            case CUSTOMER -> new CustomerDAOImpl();
            case FARMER -> new FarmerDAOImpl();
            case SUPPLIER -> new SupplierDAOImpl();
            case EMPLOYEE -> new EmployeeDAOImpl();
            case RAW_PADDY -> new RawPaddyDAOImpl();
            case USER -> new UserDAOImpl();
            case MILLING_PROCESS -> new MillingProcessDAOImpl();
            case FINISHED_PRODUCT -> new FinishedProductDAOImpl();
            case INVENTORY -> new InventoryDAOImpl(); // New case
        };
    }
}