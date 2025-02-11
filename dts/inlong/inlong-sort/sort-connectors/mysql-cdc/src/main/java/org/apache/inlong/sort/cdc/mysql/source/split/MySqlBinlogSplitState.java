/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.sort.cdc.mysql.source.split;

import io.debezium.relational.TableId;
import io.debezium.relational.history.TableChanges.TableChange;
import org.apache.inlong.sort.cdc.mysql.source.offset.BinlogOffset;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * The state of split to describe the binlog of MySql table(s).
 */
public class MySqlBinlogSplitState extends MySqlSplitState {

    private final Map<TableId, TableChange> tableSchemas;
    @Nullable
    private BinlogOffset startingOffset;
    @Nullable
    private BinlogOffset endingOffset;
    private Map<TableId, String> tableDdls;

    public MySqlBinlogSplitState(MySqlBinlogSplit split) {
        super(split);
        this.startingOffset = split.getStartingOffset();
        this.endingOffset = split.getEndingOffset();
        this.tableSchemas = split.getTableSchemas();
        this.tableDdls = split.getTableDdls();
    }

    @Nullable
    public BinlogOffset getStartingOffset() {
        return startingOffset;
    }

    public void setStartingOffset(@Nullable BinlogOffset startingOffset) {
        this.startingOffset = startingOffset;
    }

    @Nullable
    public BinlogOffset getEndingOffset() {
        return endingOffset;
    }

    public void setEndingOffset(@Nullable BinlogOffset endingOffset) {
        this.endingOffset = endingOffset;
    }

    public Map<TableId, TableChange> getTableSchemas() {
        return tableSchemas;
    }

    public void recordSchema(TableId tableId, TableChange latestTableChange) {
        this.tableSchemas.put(tableId, latestTableChange);
    }

    public MySqlBinlogSplit toMySqlSplit() {
        final MySqlBinlogSplit binlogSplit = split.asBinlogSplit();
        return new MySqlBinlogSplit(
                binlogSplit.splitId(),
                getStartingOffset(),
                getEndingOffset(),
                binlogSplit.asBinlogSplit().getFinishedSnapshotSplitInfos(),
                getTableSchemas(),
                binlogSplit.getTotalFinishedSplitSize(),
                binlogSplit.isSuspended(),
                binlogSplit.getTableDdls());
    }

    @Override
    public String toString() {
        return "MySqlBinlogSplitState{"
                + "startingOffset="
                + startingOffset
                + ", endingOffset="
                + endingOffset
                + ", split="
                + split
                + '}';
    }

    public Map<TableId, String> getTableDdls() {
        return tableDdls;
    }

    public void recordTableDdl(TableId tableId, String ddl) {
        this.tableDdls.put(tableId, ddl);
    }
}
