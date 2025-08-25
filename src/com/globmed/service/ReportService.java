package com.globmed.service;

import com.globmed.patterns.visitor.Element;
import com.globmed.patterns.visitor.SummaryVisitor;
import com.globmed.patterns.visitor.Visitor;

/**
 *
 * @author Hansana
 */
public class ReportService {

    public void generateReport(Element element) {
        Visitor visitor = new SummaryVisitor();
        element.accept(visitor);
    }
}
