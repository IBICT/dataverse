/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dataverse;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author xyang
 */
@Stateless
@Named
public class DatasetFieldServiceBean {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    private EntityManager em;
    
    private static final String FILEMETA_NAME_QUERY = "SELECT fmf from FileMetadataField fmf where fmf.name= :fieldName ";
    private static final String FILEMETA_NAME_FORMAT_QUERY = "SELECT fmf from FileMetadataField fmf where fmf.name= :fieldName and fmf.fileFormatName= :fileFormatName ";
 
    
    public List<DatasetField> findAll() {
        return em.createQuery("select object(o) from DatasetField as o where o.advancedSearchField = true and o.title != '' order by o.id").getResultList();
    }
    
    public List findAvailableFileMetadataFields() {
        List <FileMetadataField> fileMetadataFields = null; 
        fileMetadataFields = (List <FileMetadataField>) em.createQuery("SELECT fmf from FileMetadataField fmf ORDER by fmf.id").getResultList();
        
        return fileMetadataFields;
    }
    
    public List<FileMetadataField> findFileMetadataFieldByName (String name) {
        List<FileMetadataField> fmfs = null; 
        try {
            fmfs = (List<FileMetadataField>) em.createQuery(FILEMETA_NAME_QUERY).setParameter("fieldName",name).getResultList();
        } catch (Exception ex) {
            // getResultList() can throw an IllegalStateException.
            // - we just return null.
            return null; 
        }
        // If there are no results, getResultList returns an empty list. 
        return fmfs; 
    }
    
    public FileMetadataField findFileMetadataFieldByNameAndFormat (String fieldName, String formatName) {
        FileMetadataField fmf = null; 
        try {
            Query query = em.createQuery(FILEMETA_NAME_FORMAT_QUERY); 
            query.setParameter("fieldName", fieldName);
            query.setParameter("fileFormatName", formatName);
            fmf = (FileMetadataField) query.getSingleResult();
        } catch (Exception ex) {
            // getSingleResult() can throw several different exceptions:
            // NoResultException, NonUniqueResultException, IllegalStateException...
            // - we just return null.
            return null; 
        }
        return fmf; 
    }
    
    public FileMetadataField createFileMetadataField (String fieldName, String formatName) {
        FileMetadataField fmf = new FileMetadataField(); 
        fmf.setName(fieldName);
        fmf.setFileFormatName(formatName);
        //em.persist(fmf);
        //em.flush(); 
        
        return fmf; 
    }
    
    public void saveFileMetadataField (FileMetadataField fmf) {
        em.persist(fmf);
        em.flush();
        
    }
}