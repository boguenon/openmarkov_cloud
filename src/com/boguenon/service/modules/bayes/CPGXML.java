package com.boguenon.service.modules.bayes;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.ChangeNetworkTypeEdit;
import org.openmarkov.core.action.NodeNameEdit;
import org.openmarkov.core.action.NodeStateEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PrecisionEdit;
import org.openmarkov.core.action.PurposeEdit;
import org.openmarkov.core.action.RelevanceEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.action.UnitEdit;
import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeBuilder;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.CanNotWriteNetworkToFileException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.PartitionedIntervalEdit;
import org.openmarkov.core.gui.dialog.inference.common.ScopeType;
import org.openmarkov.core.gui.util.Purpose;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.core.io.format.annotation.FormatManager;
import org.openmarkov.core.model.network.AdditionalProperties;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.DeltaPotential;
import org.openmarkov.core.model.network.potential.ExponentialHazardPotential;
import org.openmarkov.core.model.network.potential.ExponentialPotential;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.core.model.network.potential.LinearRegressionPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.ProductPotential;
import org.openmarkov.core.model.network.potential.SameAsPrevious;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.WeibullHazardPotential;
import org.openmarkov.core.model.network.potential.canonical.MaxPotential;
import org.openmarkov.core.model.network.potential.canonical.MinPotential;
import org.openmarkov.core.model.network.potential.canonical.TuningPotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.model.network.type.plugin.NetworkTypeManager;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.dbgenerator.DBGenerator;
import org.openmarkov.inference.tasks.VariableElimination.VECEAGlobal;
import org.openmarkov.sensitivityanalysis.gui.model.AnalysisType;
import org.openmarkov.sensitivityanalysis.gui.model.ParameterType;
import org.openmarkov.sensitivityanalysis.gui.model.SensitivityAnalysisConfiguration;

import com.boguenon.utility.ClassUtils;
import com.boguenon.utility.OSUtil;
import com.boguenon.utility.XMLTransform;
import com.boguenon.rpc.bogServer;

import com.boguenon.service.modules.bayes.common.DiscretizeTablePanel;
import com.boguenon.service.modules.bayes.common.TablePotentialPanel;
import com.boguenon.service.modules.bayes.costeffectiveness.CEDecisionResults;
import com.boguenon.service.modules.bayes.costeffectiveness.CEPDialog;
import com.boguenon.service.modules.bayes.costeffectiveness.CostEffectivenessResults;
import com.boguenon.service.modules.bayes.costeffectiveness.InferenceOptionsDialog;
import com.boguenon.service.modules.bayes.costeffectiveness.SensitivityAnalysisController;
import com.boguenon.service.modules.bayes.editor.EditorPanel;
import com.boguenon.service.modules.bayes.editor.FSVariableBox;
import com.boguenon.service.modules.bayes.editor.InnerBox;
import com.boguenon.service.modules.bayes.editor.VisualNetwork;
import com.boguenon.service.modules.bayes.editor.VisualNode;
import com.boguenon.service.modules.bayes.editor.VisualState;
import com.boguenon.service.modules.bayes.io.PGMXWriter;
import com.boguenon.service.modules.bayes.learning.LearnNetwork;


public class CPGXML
{
	private ProbNetInfo probNetInfo = null;
	private ProbNet probNet = null;
	private Map<Node, String> _probsid = null;
	public static NetworkTypeManager networkTypeManager;
	
	private bogServer svr = null;	
	private String instanceid;
	
	private EditorPanel editorPanel = null;
	
	/**
     * Constant that represents the Edition Working Mode.
     */
    public static final int   EDITION_WORKING_MODE   = 0;
    /**
     * Constant that represents the Inference Working Mode.
     */
    public static final int   INFERENCE_WORKING_MODE = 1;
    
    private int workingMode = EDITION_WORKING_MODE;
    
    private static boolean b_system_path = false;
	
	public CPGXML(bogServer svr, String pgmx, String instanceid) {
		this.instanceid = instanceid;
		this.svr = svr;
		
		if (pgmx != null)
		{
			this.probNetInfo = this.parsePG(svr, pgmx);
			this.prepareProbNet();
		}
	}
	
	public void transform(StringBuilder sb)
	{
		PGMXWriter pwriter = new PGMXWriter(this.probNet, this._probsid, this.instanceid);
		pwriter.transform(sb);
	}
	
	private ProbNetInfo parsePG(bogServer svr, String pgmx)
	{
		String fileExtension = "pgmx";
		
		ProbNetInfo probNetInfo = null;
		
		String netname = ClassUtils.generateUID();
		String tempfile = svr.BOG_HOME + "/temp/" + netname + "." + fileExtension;
		
		try
		{
			System.out.println(">> writing temporary file");
			
			OSUtil.writeToFile(new File(tempfile), pgmx);
						
			loadOpenMarkovPlugins(svr.servlet_context_path);
			
			FormatManager formatManager = FormatManager.getInstance();
			
			if (formatManager == null)
			{
				System.err.println("Error: classpath not loaded with openmarkov (formatManager)");
			}
			
			ProbNetReader probNetReader = formatManager.getProbNetReader(fileExtension);
			
			if (CPGXML.b_system_path == false)
			{
				CPGXML.b_system_path = true;
			}
			
			if (probNetReader == null)
			{
				System.err.println("Error: classpath not loaded with openmarkov (probNetReader)");
			}
			
			probNetInfo = probNetReader.loadProbNet(tempfile);
			
			if (probNetInfo == null) {
				System.out.println("NetsIO.openNetworkFile from "
						+ netname + ": probNet null");
			}
			else
			{
				this.probNet = probNetInfo.getProbNet();
				this.probNet.setName(new File(tempfile).getName());
				
                if (editorPanel == null)
                {
                    // TODO OOPN start
                    if (probNet instanceof OOPNet)
                    {
                        // editorPanel = new EditorPanel (this, new VisualOONetwork ((OOPNet) probNet));
                    }
                    else
                    {
                        // TODO OOPN end
                        editorPanel = new EditorPanel(this, new VisualNetwork(probNet));
                    }
                }
                
                List<EvidenceCase> evidence = probNetInfo.getEvidence();
                if (evidence != null && !evidence.isEmpty()) {
                    EvidenceCase preResolutionEvidence = evidence.get(0);
                    evidence.remove(0);
                    this.editorPanel.setEvidence(preResolutionEvidence, evidence);
                }
                
                // this.setWorkingMode(CPGXML.EDITION_WORKING_MODE);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			OSUtil.deleteFile(tempfile);
		}
		
		return probNetInfo;
	}
	
	private static void loadOpenMarkovPlugins(String servlet_context_path)
	{
		if (CPGXML.b_system_path == false)
		{
			String openmarkov_class_path = System.getProperty("openmarkov.class.path", "");
			
			if (openmarkov_class_path.length() == 0)
			{
				String spath = servlet_context_path;
				String fsep = File.separator;
				
				if (spath.endsWith(fsep) == true)
				{
					spath = spath.substring(0,  spath.length() - fsep.length());
				}
				
				String classpath = "";
				String jar1 = spath + fsep + "WEB-INF" + fsep + "lib" + fsep + "org.openmarkov.bg-0.2.0.jar";
				
				File f1 = new File(jar1);
				
				if (f1.exists() == false)
				{
					System.out.println(".. jar not found");
				}
				
				classpath += jar1;
				
				System.setProperty("openmarkov.class.path", classpath);
			}
		}
	}
	
	private void prepareProbNet()
	{
		if (this.probNet != null)
		{
			this._probsid = new HashMap<Node, String>();
			
			List<Node> nodes = this.probNet.getNodes();
			
			for (int i=0; i < nodes.size(); i++)
			{
				String sid = ClassUtils.generateUID();
				_probsid.put(nodes.get(i), sid);
			}
		}
	}
	
	public ProbNet getProbNet()
	{
		return this.probNet;
	}
	
	public void setProbNet(ProbNet value)
	{
		this.probNet = value;
		this.prepareProbNet();
	}

	protected void getAdditionalPropertiesElement(StringBuilder sb, AdditionalProperties properties) {
        sb.append("<additional_properties>");
        for (String propertyName : properties.getKeySet()) 
        {
        	sb.append("<property name='" + propertyName + "' value='" + properties.get(propertyName).toString() + "'></property>");
        }
        sb.append("</additional_properties>");
    }
	
	
	public static String getShape(NodeType nodetype)
	{
		String shape = "circle";
		
		if (nodetype == NodeType.CHANCE)
		{
			shape = "circle";
		}
		else if (nodetype == NodeType.DECISION)
		{
			shape = "rect";
		}
		else if (nodetype == NodeType.UTILITY)
		{
			shape = "rhombus";
		}
		
		return shape;
	}
	
	protected PotentialType getPotentialType(Potential potential) {
        @SuppressWarnings("rawtypes")
        Class potentialClass = potential.getClass();
        if (potentialClass == TablePotential.class) {
            return PotentialType.TABLE;
        } else if ((potentialClass == MaxPotential.class) || (potentialClass == MinPotential.class)
                || (potentialClass == TuningPotential.class)) {
            return PotentialType.ICIMODEL;
        } else if (potentialClass == TreeADDPotential.class) {
            return PotentialType.TREE_ADD;
        } else if (potentialClass == UniformPotential.class) {
            return PotentialType.UNIFORM;
        } else if (potentialClass == SameAsPrevious.class) {
            return PotentialType.SAME_AS_PREVIOUS;
        } else if (potentialClass == CycleLengthShift.class) {
            return PotentialType.CYCLE_LENGTH_SHIFT;
        } else if (potentialClass == SumPotential.class) {
            return PotentialType.SUM;
        } else if (potentialClass == ProductPotential.class) {
            return PotentialType.PRODUCT;
        } else if (potentialClass == WeibullHazardPotential.class) {
            return PotentialType.WEIBULL_HAZARD;
        } else if (potentialClass == ExponentialHazardPotential.class) {
            return PotentialType.EXPONENTIAL_HAZARD;
        } else if (potentialClass == LinearRegressionPotential.class) {
            return PotentialType.LINEAR_REGRESSION;
        } else if (potentialClass == DeltaPotential.class) {
            return PotentialType.DELTA;
        } else if (potentialClass == ExponentialPotential.class) {
            return PotentialType.EXPONENTIAL;
        }
        // TODO To be extended with more potentials types
        return null;
    }
	
	public static List<String> getEnumValues(bogServer svr, String enum_name)
	{
		List<String> type_names = null;
		
		if (enum_name.equals("network_types"))
		{
			loadOpenMarkovPlugins(svr.servlet_context_path);
			
			if (CPGXML.networkTypeManager == null)
			{
				CPGXML.networkTypeManager = new NetworkTypeManager();
			}
			
			Set<String> networkTypeNames = CPGXML.networkTypeManager.getNetworkTypeNames();
			Iterator<String> itr = networkTypeNames.iterator();
			
			type_names = new ArrayList<> (networkTypeNames.size());
	        
	        while (itr.hasNext())
	        {
	        	String networkType = itr.next();
	        	type_names.add(networkType);
	        }
	        
	        Collections.sort (type_names);
	        String[] networkTypeArray = new String[type_names.size ()];
	        type_names.toArray (networkTypeArray);
		}
		else if (enum_name.equals("role_types"))
		{
			NodeType all[] = NodeType.values();
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].toString());
			}
		}
		else if (enum_name.equals("variable_types"))
		{
			VariableType all[] = VariableType.values();
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].toString());
			}
		}
		else if (enum_name.equals("purpose_types"))
		{
			String all[] = Purpose.getListStrings(true);
			
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].toString());
			}
		}
		else if (enum_name.equals("analysis_types"))
		{
			AnalysisType all[] = AnalysisType.values();
			
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].name());
			}
		}
		else if (enum_name.equals("deterministic_axis_variation_type"))
		{
			DeterministicAxisVariationType all[] = DeterministicAxisVariationType.values();
			
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].name());
			}
		}
		else if (enum_name.equals("scope_types"))
		{
			ScopeType all[] = ScopeType.values();
			
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].name());
			}
		}
		else if (enum_name.equals("potential_types"))
		{
			PotentialType all[] = PotentialType.values();
			type_names = new ArrayList<String>();
			
			for (int i=0; i < all.length; i++)
			{
				type_names.add(all[i].name());
			}
		}
		
		return type_names;
	}
		
	public int getWorkingMode ()
    {
        return workingMode;
    }
	
	/**
     * Changes the current working mode.
     * @param workingMode new value of the working mode.
     */
    public void setWorkingMode(int workingMode)
    {
        this.workingMode = workingMode;
        editorPanel.setWorkingMode(workingMode);
        // TODO OOPN
        if (probNet instanceof OOPNet)
        {
            editorPanel.setProbNet((workingMode == INFERENCE_WORKING_MODE) ? ((OOPNet) probNet).getPlainProbNet () : probNet);
        }
        
        if (workingMode == CPGXML.INFERENCE_WORKING_MODE)
        {
        	editorPanel.updateIndividualProbabilities();
        }
        else
        {
        	if (editorPanel.getInferenceAlgorithm() != null) 
        	{
        		editorPanel.setInferenceAlgorithm(null);
            }
        }
        
        editorPanel.updateNodesExpansionState(workingMode);
    }
    
    public void setNewFinding(String nodename, String statename)
    {
    	try
    	{
	    	VisualNode visualNode = this.editorPanel.visualNetwork.getNode(nodename);
	    	InnerBox innerBox = visualNode.getInnerBox();
	    	VisualState visualState = null;
	    	if (innerBox instanceof FSVariableBox)
	    	{
	    		visualState = ((FSVariableBox) innerBox).getVisualState(statename);
	    	}
	    	
	    	editorPanel.setNewFinding(visualNode, visualState);
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public void setDecisionTree(StringBuilder sb)
    {
    	DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree(this.probNet);
    	
    	sb.append("<smsg><decision_tree>");
    	
    	this.buildDecisionTreeNode(root, sb);
    	
    	sb.append("</decision_tree></smsg>");
    }
    
    private void buildDecisionTreeNode(DecisionTreeElement treeElement, StringBuilder sb)
    {
    	String elname = "";
        if(treeElement instanceof DecisionTreeNode)
        {
        	elname = "decision_treenode";
        	sb.append("<" + elname + ">");
        	DecisionTreeNode node = (DecisionTreeNode) treeElement;
        	
        	sb.append("<property>");
        	sb.append("<param name='nodetype'><![CDATA[" + node.getNodeType().toString() + "]]></param>");
        	sb.append("<param name='nodename'><![CDATA[" + node.getVariable().getName() + "]]></param>");
        	
        	if (node.getNodeType() == NodeType.UTILITY)
        	{
        		sb.append("<param name='utility'><![CDATA[" + treeElement.getUtility() + "]]></param>");
        	}
        	
        	sb.append("</property>");
        }
        else if (treeElement instanceof DecisionTreeBranch)
        {
        	elname = "decision_branch";
        	sb.append("<" + elname + ">");
        	
        	DecimalFormat df = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));
        	
        	DecisionTreeBranch treeBranch = (DecisionTreeBranch) treeElement;
        	DecisionTreeNode parent = treeBranch.getParent();
        	
        	sb.append("<property>");
        	
            if(parent != null && parent.getNodeType() == NodeType.DECISION)
            {
                if(parent.isBestDecision(treeBranch)) 
                {
                    sb.append("<param name='is_best_decistion'>T</param>");
                }
                else 
                {
                	sb.append("<param name='is_best_decistion'>F</param>");                   
                }
            }
            
            if (treeBranch.getBranchVariable() != null)
            {
            	sb.append("<param name='branch_variable'><![CDATA[" + treeBranch.getBranchVariable().getName() + "]]></param>");
            	sb.append("<param name='branch_state'><![CDATA[" + treeBranch.getBranchState().getName() + "]]></param>");
            }
            
            if(parent != null && parent.getNodeType () == NodeType.CHANCE)
            {
            	sb.append("<param name='branch_probability'><![CDATA[" + df.format(treeBranch.getBranchProbability()) + "]]></param>");
            }
            
            sb.append("<param name='child_utility'><![CDATA[" + df.format(treeBranch.getChild().getUtility()) + "]]></param>");
        	sb.append("</property>");
        }
        
        sb.append("<children>");
        for (DecisionTreeElement child : treeElement.getChildren ())
        {
            buildDecisionTreeNode(child, sb);
        }
        sb.append("</children>");
    	sb.append("</" + elname + ">");
    }
    
    public String toPGXMFile()
    {
    	 String result = "";

    	 try 
    	 {
//             if (saveOptions != null && saveOptions.isSavePlainNetwork()) {
//                 networkPanel.showPlainNetwork();
//             }
//             if (saveOptions != null
//                     && saveOptions.isSaveClassesInFile()
//                     && networkPanel.getProbNet() instanceof OOPNet) {
//                 ((OOPNet) networkPanel.getProbNet()).fillClassList();
//             }
             
    		 String fileExtension = "pgmx";
    		 String fileName = this.svr.BOG_HOME + "/temp/" + ClassUtils.generateUID();
    		 
             FormatManager formatManager = FormatManager.getInstance ();
             ProbNetWriter probNetWriter = formatManager.getProbNetWriter (fileExtension);
             try
             {
                 probNetWriter.writeProbNet (fileName, this.probNetInfo.getProbNet(), this.probNetInfo.getEvidence());
                 
                 File f = new File(fileName);
                 result = OSUtil.readFile(f);
                 
                 f.delete();
                 /*
                  * if (fileExtension.contentEquals("elv")) {
                  * //ElviraWriter.getUniqueInstance().writeProbNet(fileName,
                  * network); } else if (fileExtension.contentEquals("xml")) {
                  * //XMLWriter.getUniqueInstance().writeProbNet(fileName, network);
                  * } else if (fileExtension.contentEquals("pgmx")) {
                  * PGMXWriter.getUniqueInstance().writeProbNet(fileName, network); }
                  * else if (fileExtension.contentEquals("bif")) {
                  * //HuginWriter.getUniqueInstance().writeProbNet(fileName,
                  * network); } else { throw new
                  * NotRecognisedNetworkFileExtensionException(fileName); } } catch
                  * (IOException ex) { throw new
                  * CanNotWriteNetworkToFileException(fileName); } 
                  */
             }
             catch (WriterException ex)
             {
                 throw new CanNotWriteNetworkToFileException (fileName);
             }
             
         } 
    	 catch (CanNotWriteNetworkToFileException e) 
    	 {
             System.err.println("ErrorSavingNetwork");
         } 
    	 catch (Exception e) 
    	 {
             System.err.println("Generic I/O error");
         }
    	 
    	 return result;
    }
    
    public static String get_default_pgmx()
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	sb.append("<ProbModelXML formatVersion=\"0.2.0\">\n");
    	sb.append("</ProbModelXML>");
    	
    	return sb.toString();
    }
    
    public void loadBayesModel(XMLTransform xdata)
    {
    	org.w3c.dom.Node rnode = xdata.getNode("/smsg/item");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	sb.append("<ProbModelXML formatVersion=\"0.2.0\">\n");
    	
    	if (rnode != null)
    	{
    		org.w3c.dom.Node pnode = XMLTransform.GetXMLSubNode(rnode, "nodes");
    		String probnet_type = "BayesianNetwork";
    		
    		if (pnode != null)
    		{
    			probnet_type = XMLTransform.GetElementValue(pnode, "typename");
    			
    			probnet_type = probnet_type != null && probnet_type.length() > 0 ? probnet_type : "BayesianNetwork";
    		}
    		
    		sb.append("<ProbNet type=\"" + probnet_type + "\">\n");
    		
    		org.w3c.dom.Node tnode = null;
    		org.w3c.dom.Node snode = null;
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "probnet/comment");
    		
    		if (tnode != null)
    		{
    			String comment = XMLTransform.GetTextContent(tnode);
    			
    			if (comment != null && comment.length() > 0)
    			{
    				sb.append("<Comment><![CDATA[" + comment + "]]></Comment>");
    			}
    		}
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "probnet/additional_properties");
    		
    		if (tnode != null)
    		{
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			
    			sb.append("<AdditionalProperties>\n");
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				String pname = XMLTransform.GetElementValue(tnodes.get(i), "name");
    				String pvalue = XMLTransform.GetElementValue(tnodes.get(i), "value");
    				sb.append("<Property name=\"" + pname + "\" value=\"" + pvalue + "\"/>\n");
    			}
    			
    			sb.append("</AdditionalProperties>\n");
    		}
    		
    		sb.append("<Variables>\n");
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "nodes");
    		
    		Map<String, String> nodemap = new HashMap<String, String>();
    		
    		if (tnode != null)
    		{
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				sb.append(translateNode2Variable(tnodes.get(i), nodemap));
    			}
    		}
    		
    		sb.append("</Variables>\n");
    		
    		sb.append("<Links>\n");
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "links");
    		
    		if (tnode != null)
    		{
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				String from = XMLTransform.GetElementValue(tnodes.get(i), "from");
    				String to = XMLTransform.GetElementValue(tnodes.get(i), "to");
    				
    				boolean directed = "T".equals(XMLTransform.GetElementValue(tnodes.get(i), "directed"));
    				
    				String fmap = (nodemap.containsKey(from)) ? nodemap.get(from) : null;
    				String tmap = (nodemap.containsKey(to)) ? nodemap.get(to) : null;
    				
    				if (fmap != null && tmap != null)
    				{
	    				sb.append("<Link directed=\"" + (directed ? "true" : "false") + "\">\n");
	    		        sb.append("<Variable name=\"" + fmap + "\"/>\n");
	    		        sb.append("<Variable name=\"" + tmap + "\"/>\n");
	    		        
	    		        snode = XMLTransform.GetXMLSubNode(tnodes.get(i), "potential");
	    		        
	    		        if (snode != null)
	    		        {
	    		        	translatePotential(snode, sb);
	    		        }
	    		        
	    		        snode = XMLTransform.GetXMLSubNode(tnodes.get(i), "revealing_condition");
	    		        
	    		        if (snode != null)
	    		        {
	    		        	sb.append("<RevelationCondition>");
	    		        	
	    		        	org.w3c.dom.Node states_node = XMLTransform.GetXMLSubNode(snode, "states");
	    		        	
	    		        	if (states_node != null)
	    		        	{
	    		        		List<org.w3c.dom.Node> states_nodes = XMLTransform.GetChildNode(states_node);
	    		        		
	    		        		for (int j=0; j < states_nodes.size(); j++)
	    		        		{
	    		        			String stname = XMLTransform.GetElementValue(states_nodes.get(j), "name");
	    		        			sb.append("<State name='" + stname + "'></State>");
	    		        		}
	    		        	}
	    		        	
	    		        	sb.append("</RevelationCondition>");
	    		        }
	    		        
	    		        sb.append("</Link>\n");
    				}
    			}
    		}
    		sb.append("</Links>\n");
    		
    		sb.append("<Potentials>\n");
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "potentials");
    		
    		if (tnode != null)
    		{
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				translatePotential(tnodes.get(i), sb);
    			}
    		}
    		
    		sb.append("</Potentials>\n");
    		
    		tnode = XMLTransform.GetXMLSubNode(rnode, "decision_criteria");
    		
    		if (tnode != null)
    		{
    			sb.append("<DecisionCriteria>");
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				String cname = XMLTransform.GetElementValue(tnodes.get(i), "name");
    				sb.append("<Criteria name='" + cname + "'>");
    				
    				org.w3c.dom.Node anode = XMLTransform.GetXMLSubNode(tnodes.get(i), "additional_properties");
    				
    				if (anode != null)
    				{
    					sb.append("<AdditionalProperties>");
    					List<org.w3c.dom.Node> anodes = XMLTransform.GetChildNode(anode);
    					
    					for (int j=0; j < anodes.size(); j++)
    					{
    						String pname = XMLTransform.GetElementValue(anodes.get(j), "name");
    						String pvalue = XMLTransform.GetElementValue(anodes.get(j), "value");
    						
    						sb.append("<Property name='" + pname + "' value='" + pvalue + "'/>");
    					}
    					sb.append("</AdditionalProperties>");
    				}
    				
    				sb.append("</Criteria>");
    			}
    			sb.append("</DecisionCriteria>");
    		}
    		
    		sb.append("</ProbNet>\n");
    	}
    	
    	sb.append("</ProbModelXML>");
    	
    	String pgmx = sb.toString();
    	
    	this.probNetInfo = this.parsePG(svr, pgmx);
		this.prepareProbNet();
    }
    
    private void translatePotential(org.w3c.dom.Node rnode, StringBuilder sb)
    {
    	Properties p = XMLTransform.GetElements(rnode);
    	
    	sb.append("<Potential type=\"" + (p.getProperty("type")) + "\"\n");
		sb.append(" role=\"" + (p.getProperty("role")) + "\"");
		sb.append(">");
		
		org.w3c.dom.Node tnode = null;
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "variables");
		
		if (tnode != null)
		{
			sb.append("<Variables>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				String vname = XMLTransform.GetElementValue(tnodes.get(i), "name");
				sb.append("<Variable name=\"" + (vname) + "\" />");
			}
			
			sb.append("</Variables>");
		}
		
		String values = XMLTransform.GetSubNodeText(rnode, "values");
		values = (values == null) ? "" : values;
		
		sb.append("<Values>" + values + "</Values>");
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "uncertain_values");
		
		if (tnode != null)
		{
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			sb.append("<UncertainValues>");
			for (int i=0; i < tnodes.size(); i++)
			{
				String distribution = XMLTransform.GetElementValue(tnodes.get(i), "distribution");
				String vname = XMLTransform.GetElementValue(tnodes.get(i), "name");
				String vvalue = XMLTransform.GetTextContent(tnodes.get(i));
				
				sb.append("<Value");
				if (vname != null && vname.length() > 0)
				{
					sb.append(" name='" + vname + "'");
				}
				
				if (distribution != null && distribution.length() > 0)
				{
					sb.append(" distribution='" + distribution + "'");
				}
				
				sb.append(">" + (vvalue != null ? vvalue : "") + "</Value>");
			}
			sb.append("</UncertainValues>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "utility_variable");
		
		if (tnode != null)
		{
			String varname = XMLTransform.GetElementValue(tnode, "name");
			sb.append("<UtilityVariable name='" + varname + "'></UtilityVariable>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "top_variable");
		
		if (tnode != null)
		{
			String varname = XMLTransform.GetElementValue(tnode, "name");
			String timeslice = XMLTransform.GetElementValue(tnode, "timeslice");
			
			sb.append("<TopVariable name='" + varname + "'");
			if (timeslice != null && timeslice.length() > 0)
			{
				sb.append(" timeslice='" + timeslice + "'");
			}
			sb.append("></TopVariable>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "branches");
		
		if (tnode != null)
		{
			sb.append("<Branches>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				translateBranches(tnodes.get(i), sb);
			}
			
			sb.append("</Branches>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "model");
		
		if (tnode != null)
		{
			sb.append("<Model>" + XMLTransform.GetTextContent(tnode) + "</Model>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "subpotentials");
		
		if (tnode != null)
		{
			sb.append("<SubPotentials>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				translatePotential(tnodes.get(i), sb);
			}
			sb.append("</SubPotentials>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "time_variable");
		
		if (tnode != null)
		{
			String varname = XMLTransform.GetElementValue(tnode, "name");
			String timeslice = XMLTransform.GetElementValue(tnode, "timeslice");
			
			sb.append("<TimeVariable name='" + varname + "'");
			if (timeslice != null && timeslice.length() > 0)
			{
				sb.append(" timeslice='" + timeslice + "'");
			}
			sb.append("></TimeVariable>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "coefficients");
		
		if (tnode != null)
		{
			sb.append("<Coefficients>" + XMLTransform.GetTextContent(tnode) + "</Coefficients>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "covariates");
		
		if (tnode != null)
		{
			sb.append("<Covariates>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				sb.append("<Covariate>" + XMLTransform.GetTextContent(tnodes.get(i)) + "</Covariate>");
			}
			
			sb.append("</Covariates>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "covariance_matrix");
		
		if (tnode != null)
		{
			sb.append("<CovarianceMatrix>" + XMLTransform.GetTextContent(tnode) + "</CovarianceMatrix>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "cholesky_decomposition");
		
		if (tnode != null)
		{
			sb.append("<CholeskyDecomposition>" + XMLTransform.GetTextContent(tnode) + "</CholeskyDecomposition>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "numeric_value");
		
		if (tnode != null)
		{
			sb.append("<NumericValue>" + XMLTransform.GetTextContent(tnode) + "</NumericValue>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "state");
		
		if (tnode != null)
		{
			sb.append("<State>" + XMLTransform.GetTextContent(tnode) + "</State>");
		}
		
		sb.append("</Potential>\n");
    }
    
    private void translateBranches(org.w3c.dom.Node rnode, StringBuilder sb)
    {
    	org.w3c.dom.Node tnode = null;
    	
    	sb.append("<Branch>");
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "thresholds");
		
		if (tnode != null)
		{
			sb.append("<Thresholds>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				sb.append("<Threshold value='" + XMLTransform.GetElementValue(tnodes.get(i), "value") + "' belongsTo='" + XMLTransform.GetElementValue(tnodes.get(i),  "belongs_to") + "'></Threshold>");
			}
			
			sb.append("</Thresholds>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "states");
		
		if (tnode != null)
		{
			sb.append("<States>");
			
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			for (int i=0; i < tnodes.size(); i++)
			{
				sb.append("<State name='" + XMLTransform.GetElementValue(tnodes.get(i), "name") + "'></State>");
			}
			
			sb.append("</States>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "label");
		
		if (tnode != null)
		{
			sb.append("<Label>" + XMLTransform.GetTextContent(tnode) + "</Label>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "reference");
		
		if (tnode != null)
		{
			sb.append("<Reference>" + XMLTransform.GetTextContent(tnode) + "</Reference>");
		}
		
		tnode = XMLTransform.GetXMLSubNode(rnode, "potential");
		
		if (tnode != null)
		{
			this.translatePotential(tnode, sb);
		}
    	
    	sb.append("</Branch>");
    }
    
    private String translateNode2Variable(org.w3c.dom.Node rnode, Map<String, String> nodemap)
    {
    	String r = "<Variable";
    	
    	Properties p = XMLTransform.GetElements(rnode);
    	
    	org.w3c.dom.Node tnode = null;
    	
    	String sid = p.getProperty("sid");
    	String pname = XMLTransform.GetSubNodeText(rnode, "label");
    	
    	nodemap.put(sid,  pname);
    	
    	r += " name=\"" + pname + "\"";
    	r += " type=\"" + (p.containsKey("type") ? p.getProperty("type") : "") + "\"";
    	r += " role=\"" + (p.containsKey("role") ? p.getProperty("role") : "") + "\"";
    	r += ">";
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "comment");
    	
    	if (tnode != null)
    	{
    		String comment = XMLTransform.GetTextContent(tnode);
    		
    		if (comment != null && comment.length() > 0)
    		{
    			r += "<Comment><![CDATA[" + comment + "]]></Comment>";
    		}
    	}
    	
    	r += "<Coordinates x=\"" + (p.containsKey("x") ? p.getProperty("x") : "") + "\" y=\"" + (p.containsKey("y") ? p.getProperty("y") : "") + "\"/>";
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "additional_properties");
    	
    	if (tnode != null)
		{
			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
			
			r += "<AdditionalProperties>";
			
			for (int i=0; i < tnodes.size(); i++)
			{
				String ppname = XMLTransform.GetElementValue(tnodes.get(i), "name");
				String pvalue = XMLTransform.GetElementValue(tnodes.get(i), "value");
				r += "<Property name=\"" + ppname + "\" value=\"" + pvalue + "\"/>";
			}
			
			r += "</AdditionalProperties>";
		}
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "states");
    	
    	if (tnode != null)
    	{
    		List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    		
	    	r += "<States>";
	    	
	    	for (int i=0; i < tnodes.size(); i++)
			{
				String ppname = XMLTransform.GetElementValue(tnodes.get(i), "name");
				r += "<State name=\"" + ppname + "\"/>";
			}
	    	
	    	r += "</States>";
    	}
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "unit");
    	
    	if (tnode != null)
    	{
    		r += "<Unit>";
    		r += "</Unit>";
    	}
    	
    	tnode = XMLTransform.GetXMLSubNode(rnode, "precision");
    	
    	if (tnode != null)
    	{
    		String tvalue = XMLTransform.GetTextContent(tnode);
    		
    		if (tvalue != null && tvalue.length() > 0)
    		{
    			r += "<Precision>" + tvalue + "</Precision>";
    		}
    	}
    	
    	r += "</Variable>";
    	
    	return r;
    }
    
    public void refreshGraphicsInfo(StringBuilder sb)
    {
    	sb.append("<smsg><graphics>");
    	if (this.editorPanel != null)
    	{
    		List<VisualNode> visualNodes = this.editorPanel.visualNetwork.visualNodes;
    		
    		sb.append("<visualnodes>");
    		
    		for (int i=0; i < visualNodes.size(); i++)
    		{
    			VisualNode vnode = visualNodes.get(i);
    			
    			sb.append(vnode.refreshGraphicsInfo());
    		}
    		
    		sb.append("</visualnodes>");
    	}
    	sb.append("</graphics></smsg>");
    }
    
    public String learnNetwork(String dbfile)
    {
    	String r = null;
    	LearnNetwork _learner = new LearnNetwork(this, dbfile);
    	r = _learner.learnNetwork();
    	
    	return r;
    }
    
    public String costEffectivenessAnalysis(String method, org.w3c.dom.Node toption)
    {
    	String r = null;
    	
    	boolean sensitivityAnalysis = method.equals("sensitivity");
    	
    	if (sensitivityAnalysis == true)
    	{
    		r = sensitibilityAnalysis(toption);
    	}
    	else
    	{
    		if (probNet.getNetworkType() == InfluenceDiagramType.getUniqueInstance())
    		{
    			r = costEffectivenessDeterministic(toption);
    		}
    	}
    	
    	return r;
    }
    
    private String sensitibilityAnalysis(org.w3c.dom.Node toption)
    {
    	String r = null;
    	
    	EvidenceCase evidence = this.editorPanel.getPreResolutionEvidence();
    	
    	CostEffectivenessResults result = null;
    	
    	try
    	{
    		SensitivityAnalysisController controller = new SensitivityAnalysisController(probNet, evidence);
    		InferenceOptionsDialog inferenceOptionsDialog = new InferenceOptionsDialog(probNet, null);
    		
    		AnalysisType analysisType = AnalysisType.valueOf(XMLTransform.GetElementValue(toption, "analysis_type"));
    		ScopeType scopeType = ScopeType.valueOf(XMLTransform.GetElementValue(toption, "scope_type"));
    		
    		org.w3c.dom.Node anode = XMLTransform.GetXMLSubNode(toption, "axis");
    		
    		String xaxis_param= "POPP";
    		String yaxis_param = "PORV";
    		
    		if (anode != null)
    		{
    			org.w3c.dom.Node t = XMLTransform.GetXMLSubNode(anode, "horizontal_axis_parameter");
    			
    			if (t != null)
    			{
    				xaxis_param = XMLTransform.GetElementValue(t, "axitype");
    			}
    		}
    		
    		controller.getSensitivityAnalysisModel().setNumberOfIterationsSimulations(50);
    		
    		SensitivityAnalysisConfiguration config = controller.getConfiguration();

            if(analysisType.equals(AnalysisType.TORNADO_SPIDER))
            {
                config.setIsDeterministic(true);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.MULTI_PARAMETER);
                config.setCanBeGlobal(true);
                config.setCanBeDecision(false);
            } 
            else if(analysisType.equals(AnalysisType.PLOT))
            {
                config.setIsDeterministic(true);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.ONE_PARAMETER);
                config.setCanBeGlobal(true);
                config.setCanBeDecision(true);
            } 
            else if(analysisType.equals(AnalysisType.MAP))
            {
                config.setIsDeterministic(true);
                config.setIsBiaxial(true);
                config.setParameterType(ParameterType.ONE_PARAMETER);
                config.setCanBeGlobal(true);
                config.setCanBeDecision(true);
            } 
            else if(analysisType.equals(AnalysisType.ACCEPTABILITY))
            {
                config.setIsDeterministic(false);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.ONE_PARAMETER);
                config.setCanBeGlobal(false);
                config.setCanBeDecision(true);
            } 
            else if(analysisType.equals(AnalysisType.EVPI))
            {
                config.setIsDeterministic(false);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.ONE_PARAMETER);
                config.setCanBeGlobal(true);
                config.setCanBeDecision(false);
            } 
            else if(analysisType.equals(AnalysisType.SPIDER_CE))
            {
                config.setIsDeterministic(true);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.MULTI_PARAMETER);
                config.setCanBeGlobal(true);
                config.setCanBeDecision(false);
            } 
            else if(analysisType.equals(AnalysisType.ACCEPTABILITY_C))
            {
                config.setIsDeterministic(false);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.NO_PARAMETER);
                config.setCanBeGlobal(false);
                config.setCanBeDecision(true);
            } 
            else if(analysisType.equals(AnalysisType.CEPLANE))
            {
                config.setIsDeterministic(false);
                config.setIsBiaxial(false);
                config.setParameterType(ParameterType.NO_PARAMETER);
                config.setCanBeGlobal(false);
                config.setCanBeDecision(true);
            }
            
            controller.getSensitivityAnalysisModel().setAnalysisType(analysisType);
            controller.getSensitivityAnalysisModel().setScopeType(scopeType);
    		
            if(inferenceOptionsDialog.getMulticriteriaOptions().getMulticriteriaType().equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.UNICRITERION))
            {
            	controller.getConfiguration().setIsUnicriterion(true);
            }
            else
            {
            	controller.getConfiguration().setIsUnicriterion(false);
            }
    		
    		int numSimulationsTextField = 5;
            
    		EvidenceCase newPreResolutionEvidence;
            Iterator itr = null;
            
            if(!controller.getConfiguration().isDeterministic())
            {
                controller.getSensitivityAnalysisModel().setNumberOfIterationsSimulations(numSimulationsTextField);
            }
            
            Map<String, UncertainParameter> uncertainParameters = controller.getUncertainParameters();
            List<String> orderedUncertainParametersKeys = controller.getOrderedUncertainParametersKeys();
            
            List<UncertainParameter> selectedUncertainParametersXaxis = new ArrayList<UncertainParameter>();
            List<UncertainParameter> selectedUncertainParametersYaxis = null;
            
            for (int i=0; i < orderedUncertainParametersKeys.size(); i++)
            {
            	String uncertainParameterName = orderedUncertainParametersKeys.get(i);
            	selectedUncertainParametersXaxis.add(uncertainParameters.get(uncertainParameterName));
            }
            
            if(!config.isBiaxial() || !config.getParameterType().equals(ParameterType.ONE_PARAMETER))
            {
            	selectedUncertainParametersYaxis = new ArrayList<UncertainParameter>();
            }
            
            controller.getSensitivityAnalysisModel().setSelectedUncertainParametersXAxis(selectedUncertainParametersXaxis);
            controller.getSensitivityAnalysisModel().setSelectedUncertainParametersYAxis(selectedUncertainParametersYaxis);
            
            Variable decisionSelected = null;
            
            if (scopeType == ScopeType.GLOBAL)
            {
                controller.getSensitivityAnalysisModel().setDecisionVariable(decisionSelected);
            }
            
            controller.getSensitivityAnalysisModel().setSelectedScenario(new ArrayList());

            AxisVariation axis_var = null;
            
            axis_var = controller.getSensitivityAnalysisModel().getHorizontalAxisVariation();
            axis_var.setVariationType(DeterministicAxisVariationType.valueOf(xaxis_param));
            axis_var.setVariationValue(80.0d);
            
            axis_var = controller.getSensitivityAnalysisModel().getVerticalAxisVariation();
            axis_var.setVariationType(DeterministicAxisVariationType.valueOf(yaxis_param));
            axis_var.setVariationValue(80.0d);
            
            if(controller.getSensitivityAnalysisModel().getDecisionVariable() == null || controller.getSensitivityAnalysisModel().getSelectedScenario() == null || controller.getSensitivityAnalysisModel().getSelectedScenario().isEmpty())
            {
            	// return null;
            }
            
            newPreResolutionEvidence = new EvidenceCase(controller.getPreResolutionEvidence());
            itr = controller.getSensitivityAnalysisModel().getSelectedScenario().iterator();

            while (itr.hasNext())
            {
            	Finding finding = (Finding) itr.next();

            	try
	            {
	                newPreResolutionEvidence.addFinding(finding);
	            }
	            catch(Exception e)
	            {
	                System.err.println("LoadEvidence.Error.IncompatibleEvidence");
	            }
            }
            
            // finally run analysis
            controller.setPreResolutionEvidence(newPreResolutionEvidence);
            result = controller.runAnalysis();
            
            r = result.getResults();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return r;
    }
    
    private String costEffectivenessDeterministic(org.w3c.dom.Node toption)
    {
    	String r = null;
    	
    	EvidenceCase preResolutionEvidence = this.editorPanel.getPreResolutionEvidence();
    	EvidenceCase newPreResolutionEvidence = null;
    	ScopeType scopeType = ScopeType.DECISION;
    	
    	try
    	{
    		Iterator itr = null;
    		
	        if(scopeType.equals(ScopeType.GLOBAL))
	        {
	            VECEAGlobal veGlobalCEA = new VECEAGlobal(probNet, preResolutionEvidence);
	            org.openmarkov.core.model.network.CEP cep = veGlobalCEA.getCEP();
	            CEPDialog cepDialog = new CEPDialog(cep, probNet);
	            r = "<smsg><cost_effectiveness_results>";
	            r += cepDialog.getResult();
	            r += "</cost_effectiveness_results></smsg>";
	        }
	        else
	        {
		        newPreResolutionEvidence = new EvidenceCase(preResolutionEvidence);
		        // itr = scopeSelectorPanel.getSelectedFindings().iterator();
		        
		        while (itr.hasNext())
		        {
			        Finding finding = (Finding) itr.next();
			        try
			        {
			            newPreResolutionEvidence.addFinding(finding);
			        }
			        catch(Exception e)
			        {
			            System.err.println("LoadEvidence.Error.IncompatibleEvidence");
			        }
		        }
		        
		        Variable decisionVariable = null;
		        CEDecisionResults ceDecisionResults = new CEDecisionResults(probNet, newPreResolutionEvidence, decisionVariable);
		        
	        }
    	}
    	catch (NotEvaluableNetworkException e)
    	{
    		System.err.println("CostEffectivenessDeterministic.Error");
    	}
        catch (Exception e)
    	{
        	e.printStackTrace();
    	}
    	
    	return r;
    }
    
    public String updateNetwork(String action, XMLTransform xdata, Properties prop)
    {
    	String r = "<smsg><data instanceid='" + prop.getProperty("instanceid") + "'/></smsg>";
    	
    	if (action.equals("net_prop") == true)
    	{
    		// network properties dialog
    		org.w3c.dom.Node tnode = xdata.getNode("/smsg/item/nodes");
    		if (tnode != null)
    		{
    			String nettype = XMLTransform.GetElementValue(tnode, "type");
    			networkTypeChanged(nettype);
    		}
    	}
    	else if (action.equals("box_add") == true)
    	{
    		org.w3c.dom.Node tnode = xdata.getNode("/smsg/item/nodes");
    		if (tnode != null)
    		{
    			List<org.w3c.dom.Node> tnodes = XMLTransform.GetChildNode(tnode);
    			String msid = prop.getProperty("sid");
    			org.w3c.dom.Node itemnode = null;
    			
    			for (int i=0; i < tnodes.size(); i++)
    			{
    				String sid = XMLTransform.GetElementValue(tnodes.get(i), "sid");
    				
    				if (sid.equals(msid) == true)
    				{
    					itemnode = tnodes.get(i);
    					break;
    				}
    			}
    			
    			if (itemnode != null)
    			{
    				addNodeChanged(itemnode);
    			}
    		}
    	}
    	else if (action.equals("link_node") == true)
    	{
    		Map<String, Node> pmap = getProbNodeMap();
    		
    		String link_from = prop.getProperty("src");
    		String link_to = prop.getProperty("tgt");
    		
			attachLinkNode(pmap, link_from, link_to);
    	}
    	else if (action.equals("link_detach") == true)
    	{
    		Map<String, Node> pmap = getProbNodeMap();
    		
    		String link_from = prop.getProperty("src");
    		String link_to = prop.getProperty("tgt");
    		
    		detachLinkNode(pmap, link_from, link_to);
    	}
    	else if (action.equals("move_link") == true)
    	{
    		Map<String, Node> pmap = getProbNodeMap();
    		
    		String s0 = prop.getProperty("source_0");
    		String t0 = prop.getProperty("target_0");
    		String s1 = prop.getProperty("source_1");
    		String t1 = prop.getProperty("target_1");
    		
    		detachLinkNode(pmap, s0, t0);
    		attachLinkNode(pmap, s1, t1);
    	}
    	
    	return r;
    }
    
    private Map<String, Node> getProbNodeMap()
    {
    	Iterator<Node> itr = this._probsid.keySet().iterator();
		Map<String, Node> pmap = new HashMap<String, Node>();
		
		while (itr.hasNext())
		{
			Node p = itr.next();
			String pval = _probsid.get(p);
			
			pmap.put(pval, p);
		}
		
		return pmap;
    }
    
    private void attachLinkNode(Map<String, Node> pmap, String link_from, String link_to)
    {
		Node p1 = pmap.get(link_from);
		Node p2 = pmap.get(link_to);
		
		PNEdit linkEdit = null; 
		
		try 
		{
            linkEdit = new AddLinkEdit(probNet, p1.getVariable(), p2.getVariable(), true);
            
            if (linkEdit != null)
            {
            	probNet.doEdit (linkEdit);
            }
        } 
		catch (Exception ex)
		{
			ex.printStackTrace();;
            System.err.println(ex.getMessage ());
		}
    }
    
    private void detachLinkNode(Map<String, Node> pmap, String link_from, String link_to)
    {
		Node p1 = pmap.get(link_from);
		Node p2 = pmap.get(link_to);
		
		PNEdit linkEdit = null; 
		
		try 
		{
            linkEdit = new RemoveLinkEdit(probNet, p1.getVariable(), p2.getVariable(), true);
            
            if (linkEdit != null)
            {
            	probNet.doEdit (linkEdit);
            }
        } 
		catch (Exception ex)
		{
			ex.printStackTrace();;
            System.err.println(ex.getMessage ());
		}
    }
    
    private void addNodeChanged(org.w3c.dom.Node rnode)
    {
    	Properties p = XMLTransform.GetElements(rnode);
    	String nodename = p.getProperty("name");
    	String sid = p.getProperty("sid");
    	NodeType all[] = NodeType.values();
    	NodeType nodeType = NodeType.CHANCE;
    	for (int i=0; i < all.length; i++)
    	{
    		if (nodename.equals(all[i].toString()))
    		{
    			nodeType = all[i];
    			break;
    		}
    	}
    	
    	Point2D.Double position = new Point2D.Double();
    	double x = ClassUtils.isDouble(p.getProperty("x"));
    	double y = ClassUtils.isDouble(p.getProperty("y"));
    	x = Double.isNaN(x) ? 10.0d : x;
    	y = Double.isNaN(y) ? 10.0d : y;
    	position.setLocation(x, y);
    	HashSet<String> existingNames = new HashSet<String> ();
        for (Node node : probNet.getNodes())
        {
            String name = node.getName ();
            if (name.contains ("["))
            {
                existingNames.add (name.substring (0, name.indexOf (" [")));
            }
            else
            {
                existingNames.add (node.getName ());
            }
        }
        
        String nodeName = Util.getNextNodeName (nodeType, existingNames);
        State states[] = DefaultStates.getStatesNodeType(nodeType, probNet.getDefaultStates ());
        for (int i = 0; i < states.length; i++)
        {
            // states[i] = new State(GUIDefaultStates.getString (states[i].getName ()));
            states[i] = new State(states[i].getName ());
        }
        Variable variable = new Variable (nodeName, states);
        if (probNet.onlyTemporal ())
        {
            // default value
            variable.setBaseName (nodeName);
            variable.setTimeSlice (0);
        }
        List<Criterion> decisionCriteria = probNet.getDecisionCriteria();
        if (nodeType == NodeType.UTILITY && decisionCriteria != null)
        {
        	variable.setDecisionCriterion(decisionCriteria.get(0));
        }
        
        AddProbNodeEdit addProbNodeEdit = new AddProbNodeEdit (probNet, variable, nodeType, position);
        
        try
        {
            probNet.doEdit (addProbNodeEdit);
            
            if (variable != null)
            {
    	        Node pnode = this.probNet.getNode(variable.getName());
    	        this._probsid.put(pnode, sid);
            }
        }
        catch (Exception e1)
        {
            System.err.println (e1.toString ());
            e1.printStackTrace ();
            System.err.println(e1.toString ());
        }
    }
    
    private void networkTypeChanged(String networkTypeName)
    {
    	try
        {
    		NetworkType selectedNetworkType = networkTypeManager.getNetworkType (networkTypeName);
    		ChangeNetworkTypeEdit changeNetworkType = new ChangeNetworkTypeEdit (probNet, selectedNetworkType);
            probNet.doEdit(changeNetworkType);
        }
        catch (ConstraintViolationException | CanNotDoEditException
                | NonProjectablePotentialException | WrongCriterionException e)
        {
            e.printStackTrace ();
        }
        catch (DoEditException e)
        {
            // TODO maintain comboBox with the current probNet
            e.printStackTrace ();
            // if (!newNetwork){
            // System.out.println(e.getMessage (), e.getMessage (), JOptionPane.ERROR_MESSAGE);
            // It cannot be done the change selected so combobox
            // selection must be same
            // }
        }
    }
    
    public String loadEvidence()
    {
    	String r = null;
    	
    	File evidenceFile = null;
    	
    	System.out.println("Load evidence file " + evidenceFile.getAbsolutePath());
        CaseDatabaseManager caseDbManager = new CaseDatabaseManager();
        CaseDatabaseReader caseDbReader = caseDbManager.getReader(FilenameUtils.getExtension(evidenceFile.getName()));
        ProbNet currentNet = this.probNet;
        
        try 
        {
            CaseDatabase caseDatabase = caseDbReader.load(evidenceFile.getAbsolutePath());
            List<Variable> variables = caseDatabase.getVariables();
            int[][] cases = caseDatabase.getCases();
            for (int i = 0; i < cases.length; ++i) 
            {
                EvidenceCase newEvidenceCase = new EvidenceCase();
                
                for (int j = 0; j < cases[i].length; ++j) 
                {
                    Variable variable = null;
                    try 
                    {
                        // Ignore missing values
                        if (!variables.get(j).getStateName(cases[i][j]).isEmpty() && !variables.get(j).getStateName(cases[i][j]).equals("?")) 
                        {
                            variable = currentNet.getVariable(variables.get(j).getName());

                            try 
                            {
                                newEvidenceCase.addFinding(new Finding(variable,
                                        variable.getStateIndex(variables.get(j).getStateName(cases[i][j]))));
                            } 
                            catch (InvalidStateException e) 
                            {
                                System.err.println("LoadEvidence.Error.InvalidState.Text : " + e.getMessage());
                            }
                        }
                    }
                    catch (NodeNotFoundException e)
                    {
                    	System.err.println("LoadEvidence.Error.NodeNotFoundException");
                    }
                    catch (IncompatibleEvidenceException e) 
                    {
                        System.err.println("LoadEvidence.Error.IncompatibleEvidence");
                    }
                }
                
                this.editorPanel.addNewEvidenceCase(newEvidenceCase);
            }
            
            // save format extension in preferences
//            OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_LOADED_EVIDENCE_FORMAT,
//                    ((FileFilterBasic) evidenceFileChooser.getFileFilter()).getFilterExtension(),
//                    OpenMarkovPreferences.OPENMARKOV_FORMATS);
//            OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
//                    getDirectoryFileName(evidenceFileChooser.getSelectedFile().getAbsolutePath()),
//                    OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
        } catch (IOException e) {
            System.err.println("LoadEvidence.Error");
            e.printStackTrace();
        }
        
        return r;
    }
    
    public String getNodeInfo(String nodename)
    {
    	StringBuilder sb = new StringBuilder();
    	
    	try
    	{
    		Node node = this.probNet.getNode(nodename);
    		
    		if (node != null)
    		{
    			if (Util.hasLimitBracketSymbols (node.getVariable ().getStates ())
		            && (node.getVariable ().getVariableType () == VariableType.FINITE_STATES))
		        {
		            // really DISCRETIZED, so change the value of the VariableType
		            node.getVariable ().setVariableType (VariableType.DISCRETIZED);
		        }
    			
    			sb.append("<smsg><item name='" + node.getVariable().getBaseName() + "'>");
    			
    			sb.append("<node_definition relavance='" + Double.toString(node.getRelevance()) + "' purpose='" + node.getPurpose() + "' is_always_observed='" + (node.isAlwaysObserved() ? "T" : "F") + "'>");
    			String comment = node.getComment();
    			sb.append("<comment><![CDATA[" + (comment == null ? "" : comment) + "]]></comment>");
    			sb.append("</node_definition>");
    			
    			if (node.getNodeType() == NodeType.CHANCE || node.getNodeType() == NodeType.DECISION)
    			{
    				sb.append("<domain_values_table precision='" + node.getVariable().getPrecision() + "' unit='" + node.getVariable().getUnit().getString() + "' variable_type='" + node.getVariable().getVariableType() + "'>");
    				
    				if (node.getVariable().getVariableType() == VariableType.DISCRETIZED || node.getVariable().getVariableType() == VariableType.NUMERIC)
    				{
    					Object[][] tableData = new DiscretizeTablePanel().setDataFromPartitionedInterval(node.getVariable().getPartitionedInterval(), node.getVariable().getStates(), node);
    					
    					sb.append("<table_data>");
    					objectArrayToData(sb, tableData);
    					sb.append("</table_data>");
    				}
    				
    				switch (node.getVariable().getVariableType()) 
    				{
    	            case FINITE_STATES: 
    	                State[] states = node.getVariable().getStates();
    	                State[] reorderedStates = states.clone();
    	                Collections.reverse(Arrays.asList(reorderedStates));
    	                Object[][] tableData = getDataFromStates(reorderedStates);
    	                
    	                sb.append("<states>");
    	                objectArrayToData(sb, tableData);
    					sb.append("</states>");
    	                break;
    	            case NUMERIC:
    	                break;
    	            case DISCRETIZED:
    	            	break;
    	            }
    				
    				sb.append("</domain_values_table>");
    			}
    			else
    			{
    				
    			}
    			
    			sb.append("<node_parents>");
    			Object[][] tableData = fillArrayWithNodes (node.getParents());
    			objectArrayToData(sb, tableData);
    			
    			sb.append("</node_parents>");
    			
    			sb.append("<node_other_props>");
    			sb.append("</node_other_props>");
    			
    			Potential potential = node.getPotentials ().get (0);
    			PotentialType potentialType = this.getPotentialType(potential);
    			String potential_type = potentialType.name();
    			int numPotentialVariables = potential.getNumVariables();
    			PotentialRole role = potential.getPotentialRole ();
    			
    			sb.append("<probability potential_type='" + potential_type + "'>");
    			
    			if (((numPotentialVariables > 1 && role == PotentialRole.UTILITY) || (numPotentialVariables > 2 && role == PotentialRole.CONDITIONAL_PROBABILITY)))
    			{
    				// reorder button enabled;
    			}
    			else
    			{
    				// reorder button disable;
    			}
    			
    			if (potentialType == PotentialType.TABLE)
    			{
    				TablePotentialPanel tablePotentialPanel = new TablePotentialPanel(node);
    				sb.append(tablePotentialPanel.getXMLData());
    			}
    			
    			sb.append("</probability>");
    			
    			sb.append("</item>");
    			sb.append("</smsg>");
    		}
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	
    	return sb.toString();
    }
    
    public String setNodeInfo(XMLTransform x_content)
    {
    	String r = "<smsg></smsg>";
    	
    	org.w3c.dom.Node tnode = x_content.getNode("/smsg/item");
    	
    	if (tnode != null)
    	{
    		Properties pitem = XMLTransform.GetElements(tnode);
    		String oname = pitem.getProperty("name");
    		String cname = pitem.getProperty("cname");
    		
    		try
    		{
    			Node node = this.probNet.getNode(oname);
    			
    			if (cname != null && cname.length() > 0 && oname.equals(cname) == false)
        		{
					NodeNameEdit nodeNameEdit = new NodeNameEdit(node, cname);
					node.getProbNet().doEdit(nodeNameEdit);
        		}
    			
    			org.w3c.dom.Node mnode = XMLTransform.GetXMLSubNode(tnode, "node_definition");
    			org.w3c.dom.Node dnode = XMLTransform.GetXMLSubNode(tnode, "domain_values_table");
    			
    			PurposeEdit purposeEdit = new PurposeEdit(node, XMLTransform.GetElementValue(mnode, "purpose"));
    			node.getProbNet().doEdit(purposeEdit);
    			
    			double relavance = ClassUtils.isDouble(XMLTransform.GetElementValue(mnode, "relavance"));
    			
    			if (Double.isNaN(relavance) == false)
    			{
	    			RelevanceEdit relevanceEdit = new RelevanceEdit(node, relavance);
	    			node.getProbNet().doEdit(relevanceEdit);
    			}
    			
//    			TimeSliceEdit timeSliceEdit = null;
//    			
//                if (timeslice.equals("atemporal") == true) 
//                {
//                    timeSliceEdit = new TimeSliceEdit(node, Integer.MIN_VALUE);
//                } 
//                else 
//                {
//                    timeSliceEdit = new TimeSliceEdit(node, Integer.valueOf(timeslice));
//                }
//                
//                node.getProbNet().doEdit(timeSliceEdit);
                
//                NodeAgentEdit nodeAgentEdit = new NodeAgentEdit(node, agent);
//                node.getProbNet().doEdit(nodeAgentEdit);
//                
//                NodeDecisionCriteriaEdit nodeDecisionCriteriaEdit = new NodeDecisionCriteriaEdit(node, decisionCriteria);
//                node.getProbNet().doEdit(nodeDecisionCriteriaEdit);
    			
    			NumberFormat nf = NumberFormat.getNumberInstance ();
    			
    			nf.setGroupingUsed (false); // don't group by threes
    			
                if (node.getVariable ().getVariableType () == VariableType.DISCRETIZED || node.getVariable ().getVariableType () == VariableType.NUMERIC)
                {
                	double precision = ClassUtils.isDouble(XMLTransform.GetElementValue(dnode, "precision"));
        			
        			if (Double.isNaN(precision) == false)
        			{
    	    			PrecisionEdit precisionEdit = new PrecisionEdit (node, precision);
    	    			node.getProbNet ().doEdit (precisionEdit);
        			}
        			
        			UnitEdit unitEdit = new UnitEdit (node, XMLTransform.GetElementValue(dnode, "unit"));
	    			node.getProbNet().doEdit (unitEdit);
        			
                    double[] limits = node.getVariable ().getPartitionedInterval ().getLimits ();
                    boolean[] belongs = node.getVariable ().getPartitionedInterval ().getBelongsToLeftSide ();
                    
                    for (int i = 0; i < limits.length; i++)
                    {
                        if (limits[i] != Double.POSITIVE_INFINITY && limits[i] != Double.NEGATIVE_INFINITY)
                        {
                            double newLimit = Util.roundWithPrecision (limits[i], Double.toString (precision));
                            
                            if (limits[i] != newLimit)
                            {
                                limits[i] = newLimit;
                                int j = i;
                                while (j + 1 <= limits.length - 1 && limits[j] >= limits[j + 1])
                                {
                                    if (belongs[j] == false && belongs[j + 1] == true)
                                    {
                                        limits[j + 1] = limits[j];
                                    }
                                    else
                                    {
                                        if (j + 1 == limits.length - 1)
                                        {
                                            limits[j + 1] = Double.POSITIVE_INFINITY;
                                            break;
                                        }
                                        else limits[j + 1] = limits[j] + precision;
                                    }
                                    j++;
                                }
                                // previous limits
                                int k = i;
                                while (k - 1 >= 0 && limits[k] <= limits[k - 1])
                                {
                                    if (belongs[k] == true && belongs[k - 1] == false)
                                    {
                                        limits[k - 1] = limits[k];
                                    }
                                    else
                                    {
                                        if (k - 1 == 0)
                                        {
                                            limits[k - 1] = Double.NEGATIVE_INFINITY;
                                            break;
                                        }
                                        else limits[k - 1] = limits[k] - precision;
                                    }
                                    k--;
                                }
                            }
                            else
                            {
                                limits[i] = newLimit;
                            }
                        }
                    }
                    for (int m = 0; m < limits.length; m++)
                    {
                        if (limits[m] != Double.POSITIVE_INFINITY
                            && limits[m] != Double.NEGATIVE_INFINITY)
                        {
                            limits[m] = Util.roundWithPrecision (limits[m], Double.toString (precision));
                        }
                    }
                    
                    PartitionedInterval newPartitionedInterval = new PartitionedInterval (limits, belongs);
                    PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit (node, newPartitionedInterval);

                    node.getProbNet ().doEdit (partitionedIntervalEdit);
                    
                    PartitionedInterval newPartitionInterval = node.getVariable ().getPartitionedInterval ();
                    
                    State[] states = node.getVariable ().getStates ();
                    
                    // getPanel ().getDiscretizedStatesPanel ().setDataFromPartitionedInterval (newPartitionInterval, states);
                }
                
    			String option = null;
    			String state_action = null;
    			
    			// discretizeTable(StateAction.valueOf(state_action), node, option, selRow, selCol);
    		}
    		catch (ConstraintViolationException e)
			{
				e.printStackTrace();
			}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return r;
    }
    
    protected static final int    LOWER_BOUND_VALUE_COLUMN_INDEX  = 3;
    protected static final int    INTERVAL_NAME_COLUMN_INDEX      = 1;
    
    private void discretizeTable(StateAction action, Node node, String option, int selectedRow, int selectedColumn)
    	throws Exception
    {
    	if (action == StateAction.ADD)
    	{
    		Variable variable = node.getVariable();
            int newStateIndex =  variable.getNumStates();
            NodeStateEdit nodeStateEdit = new NodeStateEdit(node, StateAction.ADD, newStateIndex, option);
            node.getProbNet().doEdit(nodeStateEdit);
    	}
    	else if (action == StateAction.REMOVE)
    	{
    		NodeStateEdit nodeStateEdit = new NodeStateEdit(node, StateAction.REMOVE, selectedRow, "");
    		node.getProbNet().doEdit(nodeStateEdit);
    	}
    	else if (action == StateAction.UP || action == StateAction.DOWN)
    	{
    		NodeStateEdit nodeStateEdit = new NodeStateEdit(node, action, selectedRow, "");
    		node.getProbNet().doEdit(nodeStateEdit);
    	}
    	else if (action == StateAction.RENAME)
    	{
    		Variable variable = node.getVariable();
    		
    		boolean lower = selectedColumn == LOWER_BOUND_VALUE_COLUMN_INDEX;
    		
    		if (option instanceof String && selectedColumn == INTERVAL_NAME_COLUMN_INDEX) {
	    		String newName = option.toString();
	            NodeStateEdit nodeStateEdit = new NodeStateEdit(node, StateAction.RENAME, selectedRow, newName);
	            node.getProbNet().doEdit(nodeStateEdit);
    		}
    		else
            {
	            double newValue = ClassUtils.isDouble(option);
	            
	            // setting precision to the new value according with the
	            // precision value introduced by the user
	            double precision = variable.getPrecision();
	            double roundedValue = Util.roundWithPrecision(newValue, Double.toString(precision));
	            double[] currentLimits = variable.getPartitionedInterval().getLimits();
	            int numLimits = currentLimits.length;
	            boolean[] currentBelongsToLeft = variable.getPartitionedInterval().getBelongsToLeftSide();
	            int limitsIndex = (lower) ? numLimits - selectedRow - 2 : numLimits - selectedRow - 1;
	            // posterior limits
	            int i = limitsIndex;
	            currentLimits[i] = roundedValue;
	            while (i + 1 < currentLimits.length
	                    && currentLimits[i] >= currentLimits[i + 1]) {
	                if (!currentBelongsToLeft[i] && currentBelongsToLeft[i + 1]) {
	                    currentLimits[i + 1] = currentLimits[i];
	                } else {
	                    if (i + 1 == currentLimits.length - 1) {
	                        currentLimits[i + 1] = Double.POSITIVE_INFINITY;
	                        break;
	                    } else
	                        currentLimits[i + 1] = currentLimits[i] + precision;
	                }
	                i++;
	            }
	            // previous limits
	            int k = limitsIndex;
	            while (k - 1 >= 0 && currentLimits[k] <= currentLimits[k - 1]) {
	                if (currentBelongsToLeft[k] && !currentBelongsToLeft[k - 1]) {
	                    currentLimits[k - 1] = currentLimits[k];
	                } else {
	                    if (k - 1 == 0) {
	                        currentLimits[k - 1] = Double.NEGATIVE_INFINITY;
	                        break;
	                    } else
	                        currentLimits[k - 1] = currentLimits[k] - precision;
	                }
	                k--;
	            }
	            for (int m = 0; m < currentLimits.length; m++) {
	                if (currentLimits[m] != Double.POSITIVE_INFINITY
	                        && currentLimits[m] != Double.NEGATIVE_INFINITY) {
	                    currentLimits[m] = Util.roundWithPrecision(currentLimits[m],
	                            Double.toString(precision));
	                }
	            }
	            PartitionedInterval newPartitionedInterval = new PartitionedInterval(currentLimits,
	                    currentBelongsToLeft);
	            PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(node,
	                    newPartitionedInterval);
	            
	            node.getProbNet().doEdit(partitionedIntervalEdit);
            }
    	}
    }
    
    private void objectArrayToData(StringBuilder sb, Object[][] tableData)
    {
    	for (int i=0; i < tableData.length; i++)
		{
			String row = null;
			for (int j=0; j < tableData[i].length; j++)
			{
				Object c = tableData[i][j];
				String cs = c != null ? c.toString() : "";
				row = j == 0 ? cs : row + "\t" + cs;
			}
			sb.append("<row><![CDATA[" + row + "]]></row>");
		}
    }
    
    private static Object[][] fillArrayWithNodes (List<Node> nodes)
    {
        int i, l;
        Object[][] result;
        l = nodes.size ();
        result = new Object[l][2];
        for (i = 0; i < l; i++)
        {
            result[i][0] = "p_" + i; // internal name for the parent
            result[i][1] = nodes.get (i).getName ();
        }
        return result;
    }
    
    protected Object[][] getDataFromStates(State[] states) {
        int numColumns = 6; // key column is assigned in setData
        int rows = states.length;
        Object[][] data = new Object[rows][numColumns];
        for (int i = 0; i < rows; i++) {
            // data [i][0] = GUIDefaultStates.getString(states[i].getName());
            data[i][0] = states[i].getName();
        }
        return data;
    }
    
    public void dispose()
    {
    	this.probNetInfo = null;
    	this.probNet = null;
    }
}
