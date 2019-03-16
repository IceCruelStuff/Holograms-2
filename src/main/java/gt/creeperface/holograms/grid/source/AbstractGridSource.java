package gt.creeperface.holograms.grid.source;

import com.google.common.base.Preconditions;
import gt.creeperface.holograms.api.grid.source.GridSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author CreeperFace
 */
public abstract class AbstractGridSource<T extends Object> implements GridSource<T> {

    @Getter
    private final String name;

    @Getter
    private final SourceParameters parameters;

    private List<List<T>> source = new ArrayList<>();

    @Getter
    private int currentRow = -1;
    @Getter
    private int currentColumn = -1;

    private List<String> header;

    public AbstractGridSource(SourceParameters parameters) {
        Preconditions.checkNotNull(parameters, "null parameters");

        this.name = parameters.name;
        this.parameters = parameters;
    }

    @Override
    public boolean hasNextRow() {
        return (currentRow + 1) < source.size();
    }

    @Override
    public List<T> nextRow() {
        if (++currentRow >= source.size()) {
            throw new NoSuchElementException("No row found for index " + currentRow);
        }

        return source.get(currentRow);
    }

    @Override
    public boolean hasNextColumn() {
        if (currentRow < 0 || currentRow >= source.size()) {
            return false;
        }

        List<T> row = source.get(currentRow);

        if (row == null || row.isEmpty()) {
            return false;
        }

        return (currentColumn + 1) < row.size();
    }

    @Override
    public String nextColumn() {
        if (currentRow < 0 || currentRow >= source.size()) {
            throw new IllegalStateException("No row has been loaded");
        }

        List<T> row = source.get(currentRow);

        if (row == null || row.isEmpty()) {
            throw new IllegalStateException("No row has been loaded");
        }

        if (++currentColumn >= row.size()) {
            throw new NoSuchElementException("No column found for index " + currentColumn);
        }

        return Objects.toString(row.get(currentColumn));
    }

    @Override
    public int getRows() {
        return source.size();
    }

    List<List<T>> getSource() {
        return source;
    }

    protected void load(List<List<T>> data) {
        this.currentColumn = -1;
        this.currentRow = -1;
        this.source = data;
    }

    @Override
    public int getLimit() {
        return parameters.limit;
    }

    @Override
    public int getOffset() {
        return parameters.offset;
    }

    @Override
    public List<String> getHeader() {
        if (!supportsHeader()) {
            return GridSource.super.getHeader();
        }

        if (header == null) {
            throw new RuntimeException("GridSource hasn't been properly loaded");
        }

        return header;
    }

    protected void setHeader(List<String> header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof GridSource)) {
            return false;
        }

        GridSource object = (GridSource) obj;

        return object.getIdentifier().equals(this.getIdentifier()) && object.getName().equals(this.getName());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SourceParameters {

        public String name;
        public int offset;
        public int limit;

    }
}