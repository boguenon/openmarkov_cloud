package com.boguenon.service.modules.bayes.editor;

import org.openmarkov.core.model.graph.Link;

public class VisualLink {
    /**
     * Object that has the information (included visual information) of the
     * destination node.
     */
    private VisualNode         destination          = null;

    /**
     * Object that has the information (included visual information) of the
     * source node.
     */
    private VisualNode         source               = null;

    /**
     * Object that has the link information.
     */
    private Link               link                 = null;

    /**
     * Creates a new visual link from a link.
     * 
     * @param newLink
     *            object that has the information of the link.
     * @param newSource
     *            source node.
     * @param newDestination
     *            destination node.
     */
    public VisualLink(Link newLink, VisualNode newSource, VisualNode newDestination) {
        link = newLink;
        source = newSource;
        destination = newDestination;
    }

    /**
     * Returns the source node of the link.
     * 
     * @return the source node of the link.
     */
    public VisualNode getSourceNode() {

        return source;

    }

    /**
     * Returns the destination node of the link.
     * 
     * @return the destination node of the link.
     */
    public VisualNode getDestinationNode() {

        return destination;

    }

    /**
     * Sets the destination node of the link.
     * 
     * @param node
     *            the destination node of the link.
     */
    public void setDestinationNode(VisualNode node) {
        destination = node;
    }

    /**
     * Returns the link associated with the visual link.
     * 
     * @return information of the link.
     */
    public Link getLink() {

        return link;

    }

    /**
     * Paints the visual link into the graphics object.
     * 
     * @param g
     *            graphics object where paint the link.
     */
    public void paint() {
        boolean hasAbsoluteLinkRestriction = link.hasTotalRestriction();
        // setDoubleStriped(hasAbsoluteLinkRestriction);
        // setSingleStriped(link.hasRestrictions() && !hasAbsoluteLinkRestriction);
    }
}
