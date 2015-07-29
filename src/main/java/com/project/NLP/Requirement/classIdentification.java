/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *..
 */
package com.project.NLP.Requirement;

import java.util.ArrayList;

/**
 *
 * @author S. Shobiga
 */
public class classIdentification {

    ArrayList list;
    ArrayList priorToRule1;
    ArrayList classList;
    ArrayList afterRule1 = new ArrayList();
    ;
    phrasesIdentification np;
    private ArrayList attributeFromClass = new ArrayList();

    classIdentification() {

    }

    classIdentification(ArrayList list) {
        this.list = list;
        np = new phrasesIdentification(list);
        applyRules();
    }

    /*apply rules to find classes*/
    private void applyRules() {
        priorToRule1 = nounPharseIdentification("NP");
        System.out.println("Prior to rule 11: " + priorToRule1);
        afterRule1 = innerPhrases("NN");
        System.out.println("Prior to rule 1: " + priorToRule1);
    }
    /*find the noun phrases in the sentence*/

    public ArrayList nounPharseIdentification(String phrase) {
        ArrayList verb = np.getIdentifiedPhrases(phrase);
        //ArrayList v =np.innerPhrases("NN");
        //System.out.println("Class found: "+v.toString());
        return verb;
    }

    /*rule 1
     *if the phrase has 2 noun phrases first noun is identified as Class and 2nd is identified as attributes
     */
    public ArrayList innerPhrases(String p) {

        ArrayList innerPhraseList = new ArrayList();
        ArrayList splitedPhrase = new ArrayList();
        // String phraseNotation = "@"+p+"! << @"+p;
        //ArrayList list = priorToRule1;
        /* run entire list*/
        int sizeOfTheList = priorToRule1.size();
        for (int i = 0; i < sizeOfTheList; i++) {
            stanfordCoreNLP s = new stanfordCoreNLP(priorToRule1.get(i).toString());
            //stanfordCoreNLP s = new stanfordCoreNLP("the Bank client");
            ArrayList tree = s.generateTreeAnnotation();
            phrasesIdentification pI = new phrasesIdentification(tree);
            innerPhraseList = pI.getIdentifiedPhrases(p);
            if (innerPhraseList.size() == 2) {
                System.out.println("inner pharase ars: " + innerPhraseList.size());
                System.out.println("inner pharase ars: " + innerPhraseList.get(0));

                /*first noun phrase is considered as class*/
                afterRule1.add(innerPhraseList.get(0));
                //System.out.println("size: "+priorToRule1.size());
                    /*second noun phrase is considered as attribute*/
                attributeFromClass.add(innerPhraseList.get(1));

            } 
            /*rule 1 but if the the attribute is with the pronoun
            the last NP is taken as the attribute and 
            before the last element is taken as the class
            */
            else if(innerPhraseList.size()>2){
                afterRule1.add(innerPhraseList.get((innerPhraseList.size()-2)));
                attributeFromClass.add(innerPhraseList.get((innerPhraseList.size()-1)));
                
            }   
            else if (innerPhraseList.size() == 1) {
                System.out.println("working...");
                //checkPRP(pI);
                //innerPhraseList.add(priorToRule1.get(0));
            }

        }
        return afterRule1;
    }

    /*rule1 continuos...
    if the phrase consists PRP + NN identify the PRP as a class and NN as attribute
    but the PRP is stored already in class list. so store the NN as attributeFromClass
    */
    private void checkPRP(phrasesIdentification PI){
        ArrayList innerPhraseList = PI.getIdentifiedPhrases("NP");
        //ArrayList 
        
        ArrayList nn;
        //Boolean status= false;
        
        for(int i=0;i<innerPhraseList.size();i++){
            System.out.println("prp.... :"+innerPhraseList);
            if(innerPhraseList.get(i).equals("PRP")){
                System.out.println("prp.... :"+innerPhraseList.get(i));
                attributeFromClass.add(innerPhraseList.get(i+1));
                break;
            }
        }

    }
    
    public ArrayList innerPhrases1(String p) {

        ArrayList innerPhraseList = new ArrayList();
        String dummy = "";
        ArrayList splitedPhrase = new ArrayList();
        // String phraseNotation = "@"+p+"! << @"+p;
        //ArrayList list = priorToRule1;
        /* run entire list*/
        int sizeOfTheList = priorToRule1.size();
        for (int i = 0; i < sizeOfTheList; i++) {
            stanfordCoreNLP s = new stanfordCoreNLP(priorToRule1.get(i).toString());
            //stanfordCoreNLP s = new stanfordCoreNLP("the Bank client");
            ArrayList tree = s.generateTreeAnnotation();
            phrasesIdentification pI = new phrasesIdentification(tree);
            innerPhraseList = pI.getIdentifiedPhrases(p);
            for (int j = 0; j < innerPhraseList.size(); j++) {
                dummy = (String) innerPhraseList.get(j);
                System.out.println("innerPhraseList size: " + innerPhraseList.size());
                String arr[] = dummy.split(",");
                System.out.println("arr size: " + arr.length);
                if (arr.length != 0) {
                    for (int k = 0; k < arr.length; k++) {
                        System.out.println("inserted: " + arr[k]);

                        splitedPhrase.add(arr[k]);
                    }
                } else if (arr.length == 0) {
                    splitedPhrase.add(dummy);
                }
                if (splitedPhrase.size() == 2) {
                    System.out.println("inner pharase ars: " + splitedPhrase.size());
                    System.out.println("inner pharase ars: " + splitedPhrase.get(0));

                    /*first noun phrase is considered as class*/
                    afterRule1.add(splitedPhrase.get(0));
                    //System.out.println("size: "+priorToRule1.size());
                    /*second noun phrase is considered as attribute*/
                    attributeFromClass.add(splitedPhrase.get(1));

                } else if (splitedPhrase.size() == 1) {
                    //innerPhraseList.add(priorToRule1.get(0));
                }
            }

        }
        return afterRule1;
    }

    /*method to get the lists of classes*/
    public ArrayList getClasses() {
        classList = afterRule1;
        return classList;
    }

    /*method to get the lists of attributes*/
    public ArrayList getAttributeFromClass() {
        return attributeFromClass;
    }

}
