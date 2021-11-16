package petriNet.manip;
import petriNet.Node;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import petriNet.PetriNet;
import petriNet.PetriNetElement;

import petriNet.Arc;
import petriNet.ArcType;
import petriNet.place;
import petriNet.Transition;
import simplepdl.Process;
import simplepdl.SimplepdlPackage;
import simplepdl.WorkDefinition;
import simplepdl.WorkSequence;
import simplepdl.WorkSequenceType;
import simplepdl.Ressources;
import simplepdl.poids;
import petriNet.PetriNetFactory;
import petriNet.PetriNetPackage;

public class SimplePDL2PetriNet {



	
	
	// returne la somme de tous les poids associes a ces WD, sinon return 0 //
	static int sommeWorkdefinition (WorkDefinition wd) {
		int somme = 0;
		if(wd.getPoids().size() == 0) {
		  return 1;	
		}
		else {
		for (poids p : wd.getPoids()) {
			somme += p.getValeur();
		}
		return somme;
		}
	}
	
	public static void main(String[] args) {
		// Chargement du package SimplePDL afin de le transformer en petriNet.
				SimplepdlPackage packageInstancesimplepdl = SimplepdlPackage.eINSTANCE;
				
				// Enregistrer l'extension ".xmi" comme devant Ãªtre ouverte Ã 
				// l'aide d'un objet "XMIResourceFactoryImpl"
				Resource.Factory.Registry regsimplepdl = Resource.Factory.Registry.INSTANCE;
				Map<String, Object> msipmlepdl = regsimplepdl.getExtensionToFactoryMap();
				msipmlepdl.put("xmi", new XMIResourceFactoryImpl());
				
				// CrÃ©er un objet resourceSetImpl qui contiendra une ressource EMF (notre modÃ¨le)
				ResourceSet resSetsimple = new ResourceSetImpl();

				// Charger la ressource simple pdl (notre modÃ¨le)
				URI modelURIsipmlepdl = URI.createURI("models/SimplePDLCreator_Created_Process.xmi");
				Resource resourcesimplepdl = resSetsimple.getResource(modelURIsipmlepdl, true);
		// Charger le package petriNet afin de l'enregistrer dans le registre d'Eclipse.
		PetriNetPackage packageInstance = PetriNetPackage.eINSTANCE;
		
		// Enregistrer l'extension ".xmi" comme devant Ãªtre ouverte Ã 
		// l'aide d'un objet "XMIResourceFactoryImpl"
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		// CrÃ©er un objet resourceSetImpl qui contiendra une ressource EMF (le modÃ¨le)
		ResourceSet resSet = new ResourceSetImpl();

		// DÃ©finir la ressource (le modÃ¨le)
		URI modelURI = URI.createURI("models/PetrinetCreator_Created_Process.xmi");
		Resource resource = resSet.createResource(modelURI);
		
		// La fabrique pour fabriquer les Ã©lÃ©ments de petriNet
	    PetriNetFactory myFactory = PetriNetFactory.eINSTANCE;


		// RÃ©cupÃ©rer le premier Ã©lÃ©ment du modÃ¨le (Ã©lÃ©ment racine)
		Process process = (Process) resourcesimplepdl.getContents().get(0);
		
		// CrÃ©er un Ã©lÃ©ment Process
		PetriNet petri = myFactory.createPetriNet();
		// meme nom que process 
		petri.setName(process.getName());
		
		// Ajouter le petri dans le modÃ¨le
		resource.getContents().add(petri);
		
		
		// Recupérer les WorkDefinition hachmaps type
		Map<WorkDefinition, List<Node>> petrielement_list=new HashMap<WorkDefinition,List<Node>>();
		
		Map<Ressources, place> Ressource_list = new HashMap<Ressources,place>();
		
		for (Object o : process.getProcessElements()) {
			if (o instanceof Ressources) {
				Ressources r = (Ressources) o;
				//creer place , ajouter nom , set jeton
				place pr = myFactory.createplace();
				pr.setName(r.getName());
			    pr.setNb_jeton(r.getOccurences());
			    
				petri.getPetrinetElement().getNode().add(pr);
				Ressource_list.put(r, pr);
			}
		}
				for (Object o : process.getProcessElements()) {
					if (o instanceof WorkDefinition) {
						List<Node> petrielements = new ArrayList<Node>();
						WorkDefinition wd = (WorkDefinition) o;
						
						place p1 = myFactory.createplace();
					    p1.setName("Ready_"+wd.getName());
					    p1.setNb_jeton(1);
					    
					    Transition trans1 = myFactory.createTransition();
					    trans1.setName("Start_"+wd.getName());
					    
					    Arc arc1 = myFactory.createArc();
					    arc1.setToken_nb(1);
					    arc1.setSource(p1);
					    arc1.setCible(trans1);
					    arc1.setType_arc(ArcType.ARC);
					    
					    
					    Transition trans2 = myFactory.createTransition();
					    trans2.setName("Finish_"+wd.getName());
					    
					    place p2 = myFactory.createplace();
					    p2.setName("Started_"+wd.getName());
					    p2.setNb_jeton(0);
					    
					    Arc arc2 = myFactory.createArc();
					    arc2.setToken_nb(sommeWorkdefinition(wd));
					    arc2.setSource(trans1);
					    arc2.setCible(p2);
					    arc2.setType_arc(ArcType.ARC);
					    
					    place p3 = myFactory.createplace();
					    p3.setName("Finished_"+wd.getName());
					    p3.setNb_jeton(0);
					    
					    Arc arc3 = myFactory.createArc();
					    arc3.setToken_nb(sommeWorkdefinition(wd));
					    arc3.setSource(p2);
					    arc3.setCible(trans2);
					    arc3.setType_arc(ArcType.ARC);
					    Arc arc4 = myFactory.createArc();
					    arc4.setToken_nb(1);
					    arc4.setSource(trans2);
					    arc4.setCible(p3);
					    arc4.setType_arc(ArcType.ARC);
					    place p4 = myFactory.createplace();
					    p4.setName("Demarree_"+wd.getName());
					    p4.setNb_jeton(0);
					    
					    Arc arc5 = myFactory.createArc();
					    arc5.setToken_nb(1);
					    arc5.setSource(trans1);
					    arc5.setCible(p4);
					    arc5.setType_arc(ArcType.ARC);
					    for (poids p : wd.getPoids()) {
							Ressources r = p.getRessources();
							int val = p.getValeur();
							place pr = Ressource_list.get(r);
							
							Arc ressource_arc1 = myFactory.createArc();
							ressource_arc1.setToken_nb(val);
							ressource_arc1.setSource(trans2);
							ressource_arc1.setCible(pr);
							ressource_arc1.setType_arc(ArcType.ARC);
							Arc ressource_arc2 = myFactory.createArc();
							ressource_arc2.setToken_nb(val);
							ressource_arc2.setSource(pr);
							ressource_arc2.setCible(trans1);
							ressource_arc2.setType_arc(ArcType.ARC);

						    petri.getPetrinetElement().getArc().add(ressource_arc1);
						    petri.getPetrinetElement().getArc().add(ressource_arc2);
						}
				
					    
					    petri.getPetrinetElement().getNode().add(p1);
					    petri.getPetrinetElement().getNode().add(p2);
					    petri.getPetrinetElement().getNode().add(p3);
					    petri.getPetrinetElement().getNode().add(p4);
					    

					    petri.getPetrinetElement().getNode().add(trans1);
					    petri.getPetrinetElement().getNode().add(trans2);
					    
					    petri.getPetrinetElement().getArc().add(arc1);
					    petri.getPetrinetElement().getArc().add(arc2);
					    petri.getPetrinetElement().getArc().add(arc3);
					    petri.getPetrinetElement().getArc().add(arc4);
					    petri.getPetrinetElement().getArc().add(arc5);
					    petrielements.add(p3);
					    petrielements.add(p4);
					    petrielements.add(trans1);
					    petrielements.add(trans2);
					    petrielement_list.put(wd,petrielements);
					}
				}
				for (Object o : process.getProcessElements()) {
					if (o instanceof WorkSequence) {
						WorkSequence ws = (WorkSequence) o;
						WorkSequenceType typelink = ws.getLinkType();
						WorkDefinition predecessor = ws.getPredecessor();
						WorkDefinition successor = ws.getSuccessor();
						Arc readarc = myFactory.createArc();
						readarc.setType_arc(ArcType.READ_ARC);
						switch(typelink.getValue()){
							case  WorkSequenceType.FINISH_TO_FINISH_VALUE: 
									readarc.setToken_nb(1);
										// finished de predesseur jusqua finish succeseur
									readarc.setSource(petrielement_list.get(predecessor).get(0));
									readarc.setCible(petrielement_list.get(successor).get(3));
									break;
							case  WorkSequenceType.FINISH_TO_START_VALUE: 
								readarc.setToken_nb(1);
								// finished de pred to start  succ
								readarc.setSource(petrielement_list.get(predecessor).get(0));
								readarc.setCible(petrielement_list.get(successor).get(2));
								break;
							case  WorkSequenceType.START_TO_FINISH_VALUE: 
								readarc.setToken_nb(1);
								// demarrer de pred to finish succe 
								readarc.setSource(petrielement_list.get(predecessor).get(1));
								readarc.setCible(petrielement_list.get(successor).get(3));
								break;
							default : 
								readarc.setToken_nb(1);
								//demarrer de pred to start de succe
								readarc.setSource(petrielement_list.get(predecessor).get(1));
								readarc.setCible(petrielement_list.get(successor).get(2));
						}
						petri.getPetrinetElement().getArc().add(readarc);
					}
				}
				
				
	    
		// Sauver la ressource
	    try {
	    	resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}