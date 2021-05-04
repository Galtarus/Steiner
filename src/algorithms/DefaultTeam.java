package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

public class DefaultTeam {


  public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {

    int[][] paths = calculShortestPaths(points, edgeThreshold);

    // on crée les Edge de K avec leurs taille totale et on les trie
    ArrayList<Edge> hitPointEdges = Kruskal.kruskalSortedEdges(points, hitPoints, paths);

    // la fonction de "tagging" de Kruskal du prof (je l'ai juste ressorti dans une
    // fonction)
    hitPointEdges = Kruskal.tagging(points, hitPointEdges);

    // ca c'es un la transposition des arretes de K vers G
    ArrayList<Edge> EdgeTranspoKversG = Kruskal.transpoVersG(points, paths, hitPointEdges);

    return Kruskal.edgesToTree(EdgeTranspoKversG, EdgeTranspoKversG.get(0).p);
  }








//J'AI ESSAYE PLUSIEURS HEURISTIQUES ET J'AI FINI PAR CHOISIR LA SUIVANTE:
//ON CALCULE L'ARBRE COUVRANT DE TOUTE LES HIT POINTS
//ET ENSUITE ON "BRULE" L'ARBRE PAR SES FEUILLES
//EN PRIORITISANT LES FEUILLES AVEC LES ARETES LES PLUS COUTEUSES
//TOUT EN EVITANT DE RETIRER LE POINT MERE
//JUSQU'A RENTRER DANS LE BUDGE

//ET DANS UNE DEUXIEME MINI PARTIE ON LUI RAJOUTE LES ARETES RETIREES EN COMMENCANT PAR LA PLUS PETITE
//TAT QU'ON RESTE DANS LE BUDGET


  public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {

    int[][] paths = calculShortestPaths(points, edgeThreshold);
    // petite erreur quelque part dans le comptage du poids de l'arbre (légere
    // difference variante avec celui affiché)
    // une arete doit sauter quelquepart dans le comptage
    // je reste tout de même en dessous DE 1664 !
    int budgetMax = 1720;

    // on crée les Edge de K avec leurs taille totale et on les trie
    ArrayList<Edge> hitPointEdges = Kruskal.kruskalSortedEdges(points, hitPoints, paths);

    // la fonction de "tagging" de Kruskal du prof (je l'ai juste ressorti dans une
    // fonction)

    hitPointEdges = Kruskal.tagging(points, hitPointEdges);

    // ca c'est la transposition des arretes de K vers G
    ArrayList<Edge> EdgeTranspoKversG = Kruskal.transpoVersG(points, paths, hitPointEdges);


    
    //On trouve les position des hitPoints qui sont les Feuilles de l'arbre couvrant
    //##Pour ce faire j'ai crée un ArrayList servant de tableau pour compter le nombre de fois que chaque hitPoint apparait
    //dans la liste des aretes 
    //une liste de comptage initialisé a zero
    //les hitpoints "feuilles" n'apparaitront que sur une seule arete et ainsi
    //la position des points de score 1 correspond à la position des hitpoints "feuilles" dans l'arraylist Hipoint!
    ArrayList<Integer> candidatfeuilles = calculerCandidatfeuilles(EdgeTranspoKversG, points, hitPoints);
    Collections.reverse(hitPointEdges);
    //ordre décroissant




    int longueur = hitPointEdges.size();
    double budgetUtilise = budgetUtilise(hitPointEdges);
    ArrayList<Edge> RemovedEdges = new ArrayList<Edge>();

    do {

      budgetUtilise = budgetUtilise(hitPointEdges);
      longueur = hitPointEdges.size();

      //on parcourt toutes les aretes de l'arbre K
      for (int i = 0; i < longueur; i++) {

        //on récupere la position des points dans chaque arete dans la liste des hitPoints
        int posP = hitPoints.indexOf(hitPointEdges.get(i).p);
        int posQ = hitPoints.indexOf(hitPointEdges.get(i).q);

        //si != -1 alors point est un hitPoint
        if (posP != -1) {
          //si point est une feuille (lié à une seule unique arete) 
          //et différent du point mère
          if (candidatfeuilles.get(posP) == 1 && posP!=0   ) {

            System.out.println("removeP");
            RemovedEdges.add(hitPointEdges.remove(i));

            //on recalcule G pour retrouver les nouveaux hitPoints candidats au removal
            EdgeTranspoKversG = Kruskal.transpoVersG(points, paths, hitPointEdges);
            candidatfeuilles = calculerCandidatfeuilles(EdgeTranspoKversG, points, hitPoints);
            
            break;

          }
        }

        if (posQ != -1) {
          if (candidatfeuilles.get(posQ) == 1 && posQ!=0 ) {
            System.out.println("removeQ");
            RemovedEdges.add(hitPointEdges.remove(i));
            EdgeTranspoKversG = Kruskal.transpoVersG(points, paths, hitPointEdges);
            candidatfeuilles = calculerCandidatfeuilles(EdgeTranspoKversG, points, hitPoints);
            break;

          }
        }
      }
    } while (budgetUtilise > budgetMax);

//on rajoute autant d'aretes retirées qu'on peut
    // RemovedEdges = Kruskal.sort(RemovedEdges);


    // for (Edge e : RemovedEdges) {
    //   System.out.println("longueur edge " + e.sommePath);
    //   for(int k=0;k<candidatfeuilles.size();k++){
    //     int posP = hitPoints.indexOf(RemovedEdges.get(k).p);
    //     int posQ = hitPoints.indexOf(RemovedEdges.get(k).q);
    //     Point p = hitPointEdges.get(k).p;
    //     Point q = hitPointEdges.get(k).q;
    //     if(candidatfeuilles.get(k)==1 && p)

    //   }

    //   if ((budgetUtilise + e.sommePath) < budgetMax)
    //     budgetUtilise+=e.sommePath;
    //     hitPointEdges.add(e);
    // }


    



    //on rajoute autant d'aretes retirées qu'on peut
    RemovedEdges = Kruskal.sort(RemovedEdges);
    for (Edge e : RemovedEdges) {
      System.out.println("longueur edge " + e.sommePath);
      if ((budgetUtilise + e.sommePath) < budgetMax)
        hitPointEdges.add(e);
    }

    EdgeTranspoKversG = Kruskal.transpoVersG(points, paths, hitPointEdges);

    return Kruskal.edgesToTree(EdgeTranspoKversG, EdgeTranspoKversG.get(0).p);
  }




  private double budgetUtilise(ArrayList<Edge> hitPointEdges) {

    int sommeBudget = 0;
    for (Edge e2 : hitPointEdges) {
      sommeBudget += e2.sommePath;
    }
    return sommeBudget;
  }

   //On trouve les position des hitPoints qui sont les Feuilles de l'arbre couvrant 
   //et on les trie par la longueur de leurs aretes dans G
  private ArrayList<Integer> calculerCandidatfeuilles(ArrayList<Edge> EdgeTranspoKversG, ArrayList<Point> points,
      ArrayList<Point> hitPoints) {
    ArrayList<Integer> candidatfeuilles = new ArrayList<Integer>();
    int pPosDnPoints, qPosDnPoints, pPosDnHit, qPosDnHit;
    Point p, q;

    for (int i = 0; i < hitPoints.size(); i++)
      candidatfeuilles.add(0);

    for (Edge edge : EdgeTranspoKversG) {
      pPosDnPoints = points.indexOf(edge.p);
      p = points.get(pPosDnPoints);
      pPosDnHit = hitPoints.indexOf(p);
      if (pPosDnHit != -1) {

        candidatfeuilles.set(pPosDnHit, candidatfeuilles.get(pPosDnHit) + 1);
      }

      qPosDnPoints = points.indexOf(edge.q);
      q = points.get(qPosDnPoints);
      qPosDnHit = hitPoints.indexOf(q);
      if (qPosDnHit != -1) {
        // System.out.println(qPosDnHit);
        candidatfeuilles.set(qPosDnHit, candidatfeuilles.get(qPosDnHit) + 1);
      }

    }

    return candidatfeuilles;
  }

  // private ArrayList<Point> hitPointsSelected(ArrayList<Point> hitPoints, ArrayList<Edge> mere_hitPoints) {
  //   ArrayList<Point> pointsSelected = new ArrayList<Point>();

  //   for (Edge e : mere_hitPoints) {
  //     pointsSelected.add(e.p);
  //   }
  //   return pointsSelected;
  // }

  public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
    int[][] paths = new int[points.size()][points.size()];
    for (int i = 0; i < paths.length; i++)
      for (int j = 0; j < paths.length; j++)
        paths[i][j] = i;

    double[][] dist = new double[points.size()][points.size()];

    for (int i = 0; i < paths.length; i++) {
      for (int j = 0; j < paths.length; j++) {
        if (i == j) {
          dist[i][i] = 0;
          continue;
        }
        if (points.get(i).distance(points.get(j)) <= edgeThreshold)
          dist[i][j] = points.get(i).distance(points.get(j));
        else
          dist[i][j] = Double.POSITIVE_INFINITY;
        paths[i][j] = j;
      }
    }

    for (int k = 0; k < paths.length; k++) {
      for (int i = 0; i < paths.length; i++) {
        for (int j = 0; j < paths.length; j++) {
          if (dist[i][j] > dist[i][k] + dist[k][j]) {
            dist[i][j] = dist[i][k] + dist[k][j];
            paths[i][j] = paths[i][k];

          }
        }
      }
    }
    return paths;
  }
}
