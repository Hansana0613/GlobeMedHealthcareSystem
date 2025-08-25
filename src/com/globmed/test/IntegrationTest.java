package com.globmed.test;

import com.globmed.dao.AppointmentDAO;
import com.globmed.model.Appointment;
import com.globmed.model.Bill;
import com.globmed.model.Patient;
import com.globmed.patterns.chain.Request;
import com.globmed.patterns.composite.RoleComponent;
import com.globmed.service.AppointmentService;
import com.globmed.service.BillingService;
import com.globmed.service.PatientService;
import com.globmed.service.ReportService;
import com.globmed.service.RoleService;
import com.globmed.service.SecurityService;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Hansana
 */
public class IntegrationTest {

    private PatientService patientService;
    private AppointmentService appointmentService;
    private BillingService billingService;
    private RoleService roleService;
    private ReportService reportService;
    private SecurityService securityService;

    @Before
    public void setUp() {
        patientService = new PatientService();
        appointmentService = new AppointmentService();
        billingService = new BillingService();
        roleService = new RoleService();
        reportService = new ReportService();
        securityService = new SecurityService();
    }

    // A: Secure Patient Access
    @Test
    public void testSecurePatientAccess() {
        Request validRequest = new Request("carol_admin", "Admin", 1L);
        Request invalidRequest = new Request("guest", "Guest", 1L);
        Patient validPatient = patientService.getPatient(validRequest.getUsername(), validRequest.getRole(), validRequest.getPatientId());
        Patient invalidPatient = patientService.getPatient(invalidRequest.getUsername(), invalidRequest.getRole(), invalidRequest.getPatientId());
        assertNotNull(validPatient);
        assertNull(invalidPatient);
    }

    // B: Schedule Appointment
    @Test
    public void testScheduleAppointment() {
        appointmentService.bookAppointment("John Doe", "Dr. Alice", "2025-08-26 10:00", "Clinic A");
        // Verify via DAO (assume findByTime method exists)
        AppointmentDAO dao = new AppointmentDAO();
        Appointment appt = dao.findById(1L); // Placeholder ID
        assertNotNull(appt);
    }

    // C: Build/Process Bill
    @Test
    public void testBuildAndProcessBill() {
        Appointment appt = new Appointment();
        Bill bill = billingService.createBill(appt);
        assertTrue(billingService.processClaim(bill));
    }

    // D: Manage Roles
    @Test
    public void testManageRoles() {
        RoleComponent role = roleService.createRole("Doctor");
        assertTrue(role.hasPermission("View Patients"));
    }

    // E: Generate Report
    @Test
    public void testGenerateReport() {
        Patient patient = new Patient();
        patient.setName("John Doe");
        reportService.generateReport(patient); // Check console output
    }

    // F: Secure Operations
    @Test
    public void testSecureOperations() {
        String result = securityService.secureExecute("testdata");
        assertTrue(result.contains("Processed:"));
    }
}
