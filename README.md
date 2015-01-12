# ExpertFinder
An ontology-based tool for identifying experts among wiki users.

## Starting the processing

There are currently two ways to start the processing of the wiki contents and generation of reputation and expertise data.

### Programmatically

The entry point to the processing chain is the class

`de.csw.expertfinder.mediawiki.uima.deploy.MediaWikiCPERunner`

The class also has a main method for testing purposes.

### Using the the UIMA CPE Deployment Tool:

## Getting reputation and expertise values
Once the wiki's contents have been processed, the reputation and expertise scores for authors can be retrieved using the class

`de.csw.expertfinder.expertise.ExpertiseModel`

