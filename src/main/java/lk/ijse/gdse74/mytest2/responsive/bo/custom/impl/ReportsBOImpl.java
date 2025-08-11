package lk.ijse.gdse74.mytest2.responsive.bo.custom.impl;

import lk.ijse.gdse74.mytest2.responsive.bo.custom.ReportsBO;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOFactory;
import lk.ijse.gdse74.mytest2.responsive.dao.DAOTypes;
import lk.ijse.gdse74.mytest2.responsive.dao.custom.ReportsDAO;
import lk.ijse.gdse74.mytest2.responsive.dto.Reportsdto;
import lk.ijse.gdse74.mytest2.responsive.entity.ReportsEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportsBOImpl implements ReportsBO {

    private ReportsDAO reportsDAO = DAOFactory.getInstance().getDAO(DAOTypes.REPORTS);

    @Override
    public boolean saveReport(Reportsdto dto) throws SQLException, ClassNotFoundException {
        return reportsDAO.save(new ReportsEntity(dto.getReportId(), dto.getReportType(), dto.getReportDate()));
    }

    @Override
    public boolean updateReport(Reportsdto dto) throws SQLException, ClassNotFoundException {
        return reportsDAO.update(new ReportsEntity(dto.getReportId(), dto.getReportType(), dto.getReportDate()));
    }

    @Override
    public boolean deleteReport(String id) throws SQLException, ClassNotFoundException {
        return reportsDAO.delete(id);
    }

    @Override
    public Reportsdto searchReport(String id) throws SQLException, ClassNotFoundException {
        ReportsEntity entity = reportsDAO.search(id);
        if (entity != null) {
            return new Reportsdto(entity.getReportId(), entity.getReportType(), entity.getReportDate());
        }
        return null;
    }

    @Override
    public List<Reportsdto> getAllReports() throws SQLException, ClassNotFoundException {
        List<ReportsEntity> entities = reportsDAO.getAll();
        List<Reportsdto> dtoList = new ArrayList<>();
        for (ReportsEntity entity : entities) {
            dtoList.add(new Reportsdto(entity.getReportId(), entity.getReportType(), entity.getReportDate()));
        }
        return dtoList;
    }

    /*
    @Override
    public String generateNextReportId() throws SQLException, ClassNotFoundException {
        return reportsDAO.generateNextId();
    }
    */
}