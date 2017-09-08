// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.transform.product.pds3;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Statement;

/**
 * An extension of the ObjectStatement object in order to preserve
 * the ordering of the statements when they are added to the object.
 * This comes in handy in that it allows the statements to be printed
 * in order. The parent class stores statements in a Hash Map and sorts
 * it by object name.
 * 
 * @author mcayanan
 *
 */
public class OrderedObjectStatement extends ObjectStatement {
  private List<Statement> statements;
  
  /**
   * Constructor.
   * 
   * @param sourceLabel The label the object belongs to.
   * @param identifier The name of the object.
   */
  public OrderedObjectStatement(Label sourceLabel, String identifier) {
    super(sourceLabel, identifier);
    this.statements = new ArrayList<Statement>();
  }
  
  public void addStatement(Statement statement) {
    super.addStatement(statement);
    this.statements.add(statement);
  }
  
  public List<Statement> getStatements() {
    return this.statements;
  }
}
