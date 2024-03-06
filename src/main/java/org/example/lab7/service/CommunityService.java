package org.example.lab7.service;


import org.example.lab7.domain.CommunityNode;
import org.example.lab7.domain.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service used for managing the nodes of the community graph
 */
public class CommunityService {
    private Map<String, CommunityNode> nodes;

    /**
     * Constructor
     * @param allNodes, nodes from the graph
     */
    public CommunityService(List<CommunityNode> allNodes) {
        nodes = allNodes.stream()
                .collect(Collectors.toMap(node -> node.getUser().get().getId(), node -> node));
    }

    /**
     * Add a new node to the graph
     * @param newNode, CommunityNode
     */
    public void addNode(CommunityNode newNode) {
        nodes.put(newNode.getUser().get().getId(), newNode);
    }

    /**
     * Makes a dfs visit in order to search for the current component from the graph
     * @param visitedNodes, keeps track of the nodes which have already been visited
     * @param currentNode, CommunityNode, the currentNode we are in the bfs algorithm
     * @param currentComponent, CommunityNodes from the current component are stored in this
     */
    private void dfsVisit(Set<String> visitedNodes, CommunityNode currentNode, List<Optional<User>> currentComponent) {
        if (!visitedNodes.contains(currentNode.getUser().get().getId())) {
            visitedNodes.add(currentNode.getUser().get().getId());
            currentComponent.add(currentNode.getUser());
            currentNode.getFriends().forEach(friend->dfsVisit(visitedNodes, nodes.get(friend.get().getId()), currentComponent));
        }
    }

    /**
     * Builds all different communities from the graph
     *
     * @return the list of the communities
     */
    public List<List<Optional<User>>> getAllCommunities() {
        Set<String> visited = new HashSet<>();
        List<List<Optional<User>>>
                allCommunities = nodes.values().stream()
                .filter(node->!visited.contains(node.getUser().get().getId()))
                .map(node->{
                    List<Optional<User>> currentCommunity = new ArrayList<>();
                    dfsVisit(visited, node, currentCommunity);
                    return currentCommunity;
                })
                .collect(Collectors.toList());
        return allCommunities;
    }

    /**
     * Given a particular node, it finds the longest path which starts there
     * @param communityUser the current node of the search
     * @param visited keeps track of the visited users
     * @return the longest path starting from communityUser node
     */
    private int LongestPath(Optional<User> communityUser, Set<String> visited) {
        int longestPath = 0;
        visited.add(communityUser.get().getId());
        for (Optional<User> friend : nodes.get(communityUser.get().getId()).getFriends()) {
            if (!visited.contains(friend.get().getId())) {
                longestPath = Math.max(longestPath, 1 + LongestPath(friend, visited));
            }
        }
        visited.remove(communityUser.get().getId());
        return longestPath;
    }

    /**
     * Finds the longest path in a given community
     * @param community, represents a community
     * @return the longest path from the current community
     */
    private int findLongestPath(List<Optional<User>> community) {
        Set<String> visited = new HashSet<>();
        return community.stream()
                .mapToInt(user->LongestPath(user, visited))
                .max()
                .orElse(0);
    }

    /**
     * Finds the most sociable community, meaning the community with the longest path
     * @return the most sociable community
     */
    public List<Optional<User>> findMostSociableCommunity() {
        List<List<Optional<User>>> allCommunities = getAllCommunities();
        return allCommunities.stream()
                .max(Comparator.comparingInt(this::findLongestPath))
                .orElse(Collections.emptyList());
    }
}
