package lk.ijse.gdse74.mytest2.responsive.dao;

import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.FarmerDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.MachineMaintenanceDAOImpl;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.MillingProcessDAOImpl; // Import new MillingProcessDAOImpl
import lk.ijse.gdse74.mytest2.responsive.dao.custom.impl.SupplierDAOImpl;

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
            case MACHINE_MAINTENANCE -> (T) new MachineMaintenanceDAOImpl();
            case SUPPLIER -> (T) new SupplierDAOImpl();
            case MILLING_PROCESS -> (T) new MillingProcessDAOImpl(); // Added case for MILLING_PROCESS
        };
    }
}