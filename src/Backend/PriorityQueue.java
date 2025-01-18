package Backend;

import java.util.ArrayList;
import java.util.Iterator;

public class PriorityQueue implements Iterable<AppointmentNode> {
    protected final ArrayList<AppointmentNode> heap;

    public PriorityQueue() {
        this.heap = new ArrayList<>();
    }

    // Insert a new appointment node into the heap
    public void enqueue(AppointmentNode node) {
        heap.add(node); // Add the node at the end of the heap
        heapifyUp(heap.size() - 1); // Restore the heap property
    }

    // Remove and return the highest-priority appointment (max severity)
    public AppointmentNode dequeue() {
        if (isEmpty()) {
            System.out.println("Priority Queue is empty.");
            return null;
        }
        AppointmentNode root = heap.get(0); // Get the root (highest priority)
        AppointmentNode lastNode = heap.remove(heap.size() - 1); // Remove the last node

        if (!heap.isEmpty()) {
            heap.set(0, lastNode); // Move the last node to the root
            heapifyDown(0); // Restore the heap property
        }

        return root;
    }

    // Remove and return an appointment node by patient ID
    public AppointmentNode dequeueByPatientID(String patientID) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getPatient().getPatientID().equals(patientID)) {
                AppointmentNode targetNode = heap.get(i);
                AppointmentNode lastNode = heap.remove(heap.size() - 1); // Remove the last node

                if (i < heap.size()) {
                    heap.set(i, lastNode); // Move the last node to the removed position
                    heapifyDown(i); // Restore the heap property
                    heapifyUp(i); // Ensure correctness in case of upward adjustment
                }

                return targetNode;
            }
        }
        System.out.println("Patient with ID " + patientID + " not found in the queue.");
        return null;
    }

    // Peek at the highest-priority appointment without removing it
    public AppointmentNode peek() {
        if (isEmpty()) {
            System.out.println("Priority Queue is empty.");
            return null;
        }
        return heap.get(0);
    }

    public boolean dequeueByAppointmentID(String appointmentId) {
        for (int i = 0; i < heap.size(); i++) {
            AppointmentNode current = heap.get(i);

            if (current.getAppointmentId().equals(appointmentId)) {
                // Remove the node from the heap
                heap.set(i, heap.get(heap.size() - 1)); // Replace the current node with the last node
                heap.remove(heap.size() - 1); // Remove the last node
                heapifyDown(i); // Restore the heap property starting from the removed node's position
                return true; // Successfully removed
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found in the queue.");
        return false; // Not found
    }

    // Check if the priority queue is empty
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Display the current state of the priority queue
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("The queue is currently empty.");
            return;
        }

        System.out.println("Current Priority Queue (Max Heap):");
        for (AppointmentNode node : heap) {
            System.out.println("Appointment ID: " + node.getAppointmentId() +
                    ", Patient ID: " + node.getPatient().getPatientID() +
                    ", Severity: " + node.getSeverity() +
                    ", Date: " + node.getDate() +
                    ", Time: " + node.getTime());
        }
    }

    // Get the size of the priority queue
    public int size() {
        return heap.size();
    }

    // Restore the heap property by bubbling up (used after insertion)
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).getSeverity() > heap.get(parentIndex).getSeverity()) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    // Restore the heap property by bubbling down (used after deletion)
    private void heapifyDown(int index) {
        int largest = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        // Check if the left child is larger
        if (leftChild < heap.size() && heap.get(leftChild).getSeverity() > heap.get(largest).getSeverity()) {
            largest = leftChild;
        }

        // Check if the right child is larger
        if (rightChild < heap.size() && heap.get(rightChild).getSeverity() > heap.get(largest).getSeverity()) {
            largest = rightChild;
        }

        // If the largest is not the root, swap and continue heapifying
        if (largest != index) {
            swap(index, largest);
            heapifyDown(largest);
        }
    }

    // Swap two nodes in the heap
    private void swap(int i, int j) {
        AppointmentNode temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    // Implementing Iterable: Return an iterator for the heap
    @Override
    public Iterator<AppointmentNode> iterator() {
        return heap.iterator();
    }
}
