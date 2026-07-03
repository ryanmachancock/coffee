const esc = s => String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');

class RecipeCreator {
    constructor() {
        this.currentStep = 1;
        this.selectedBean = null;
        this.selectedInstruction = null;
        this.beans = [];
        this.instructions = [];

        this.init();
    }

    init() {
        this.bindEvents();
        this.loadBeans();
    }

    bindEvents() {
        // Step navigation
        document.getElementById('step1-next').addEventListener('click', () => this.goToStep(2));
        document.getElementById('step2-back').addEventListener('click', () => this.goToStep(1));
        document.getElementById('step2-next').addEventListener('click', () => this.goToStep(3));
        document.getElementById('step3-back').addEventListener('click', () => this.goToStep(2));

        // Bean events
        document.getElementById('bean-search').addEventListener('input', (e) => this.filterBeans(e.target.value));
        document.getElementById('create-bean-btn').addEventListener('click', () => this.createNewBean());

        // Instruction events
        document.getElementById('instruction-search').addEventListener('input', (e) => this.filterInstructions(e.target.value));
        document.getElementById('create-instruction-btn').addEventListener('click', () => this.createNewInstruction());

        // Recipe finalization
        document.getElementById('save-recipe-btn').addEventListener('click', () => this.saveRecipe());
        document.getElementById('create-another-btn').addEventListener('click', () => this.resetForm());
    }

    async loadBeans() {
        try {
            const response = await fetch('/api/beans', {
                headers: {
                    'Authorization': this.getAuthToken()
                }
            });

            if (response.ok) {
                this.beans = await response.json();
                this.displayBeans(this.beans);
            } else {
                this.showError('Failed to load beans');
            }
        } catch (error) {
            console.error('Error loading beans:', error);
            this.showError('Error loading beans');
        }
    }

    async loadInstructions() {
        try {
            const response = await fetch('/api/instructions', {
                headers: {
                    'Authorization': this.getAuthToken()
                }
            });

            if (response.ok) {
                this.instructions = await response.json();
                this.displayInstructions(this.instructions);
            } else {
                this.showError('Failed to load instructions');
            }
        } catch (error) {
            console.error('Error loading instructions:', error);
            this.showError('Error loading instructions');
        }
    }

    displayBeans(beans) {
        const container = document.getElementById('beans-list');

        if (beans.length === 0) {
            container.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <p>No beans found. Create your first bean!</p>
                </div>
            `;
            return;
        }

        container.innerHTML = beans.map(bean => `
            <div class="bean-item p-4 border border-gray-200 rounded-lg cursor-pointer hover:border-amber-500 hover:bg-amber-50 transition duration-200"
                 data-bean-id="${Number(bean.id)}">
                <div class="flex justify-between items-start">
                    <div>
                        <h5 class="font-semibold text-gray-800">${esc(bean.flavor)}</h5>
                        <p class="text-sm text-gray-600">${esc(bean.origin)} • ${esc(bean.roast)}</p>
                        ${bean.createdBy ? `<p class="text-xs text-gray-500 mt-1">Created by: ${esc(bean.createdBy)}</p>` : ''}
                    </div>
                    <div class="text-xs text-gray-400">
                        ${bean.isPublic ? 'Public' : 'Private'}
                    </div>
                </div>
            </div>
        `).join('');

        // Add click listeners to bean items
        container.querySelectorAll('.bean-item').forEach(item => {
            item.addEventListener('click', () => {
                const beanId = item.dataset.beanId;
                this.selectBean(beans.find(b => b.id == beanId));
            });
        });
    }

    displayInstructions(instructions) {
        const container = document.getElementById('instructions-list');

        if (instructions.length === 0) {
            container.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <p>No instructions found. Create your first instruction set!</p>
                </div>
            `;
            return;
        }

        container.innerHTML = instructions.map(instruction => `
            <div class="instruction-item p-4 border border-gray-200 rounded-lg cursor-pointer hover:border-amber-500 hover:bg-amber-50 transition duration-200"
                 data-instruction-id="${Number(instruction.id)}">
                <div class="flex justify-between items-start">
                    <div>
                        <h5 class="font-semibold text-gray-800">${esc(instruction.brewMethod)}</h5>
                        <p class="text-sm text-gray-600">${Number(instruction.gramsOfCoffee)}g coffee • ${Number(instruction.gramsOfWater)}g water • ${Number(instruction.waterTemp)}°F</p>
                        <p class="text-sm text-gray-600">Grind: ${esc(instruction.grindSize)}</p>
                        <p class="text-xs text-gray-500 mt-1 truncate">${esc(instruction.instructionSteps.substring(0, 100))}${instruction.instructionSteps.length > 100 ? '...' : ''}</p>
                    </div>
                </div>
            </div>
        `).join('');

        // Add click listeners to instruction items
        container.querySelectorAll('.instruction-item').forEach(item => {
            item.addEventListener('click', () => {
                const instructionId = item.dataset.instructionId;
                this.selectInstruction(instructions.find(i => i.id == instructionId));
            });
        });
    }

    filterBeans(searchTerm) {
        const filtered = this.beans.filter(bean =>
            bean.flavor.toLowerCase().includes(searchTerm.toLowerCase()) ||
            bean.origin.toLowerCase().includes(searchTerm.toLowerCase()) ||
            bean.roast.toLowerCase().includes(searchTerm.toLowerCase())
        );
        this.displayBeans(filtered);
    }

    filterInstructions(searchTerm) {
        const filtered = this.instructions.filter(instruction =>
            instruction.brewMethod.toLowerCase().includes(searchTerm.toLowerCase()) ||
            instruction.grindSize.toLowerCase().includes(searchTerm.toLowerCase()) ||
            instruction.instructionSteps.toLowerCase().includes(searchTerm.toLowerCase())
        );
        this.displayInstructions(filtered);
    }

    async createNewBean() {
        const flavor = document.getElementById('new-bean-flavor').value.trim();
        const origin = document.getElementById('new-bean-origin').value.trim();
        const roast = document.getElementById('new-bean-roast').value;

        if (!flavor || !origin || !roast) {
            this.showError('Please fill in all bean fields');
            return;
        }

        const beanData = { flavor, origin, roast };

        try {
            const response = await fetch('/api/beans', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                },
                body: JSON.stringify(beanData)
            });

            if (response.ok) {
                const newBean = await response.json();
                this.selectBean(newBean);
                this.clearNewBeanForm();
                this.showSuccess('Bean created successfully!');
                // Reload beans to show the new one
                await this.loadBeans();
            } else {
                const error = await response.text();
                this.showError(`Failed to create bean: ${error}`);
            }
        } catch (error) {
            console.error('Error creating bean:', error);
            this.showError('Error creating bean');
        }
    }

    async createNewInstruction() {
        const brewMethod = document.getElementById('new-instruction-brewmethod').value;
        const waterTemp = parseInt(document.getElementById('new-instruction-watertemp').value);
        const grindSize = document.getElementById('new-instruction-grindsize').value;
        const gramsOfCoffee = parseInt(document.getElementById('new-instruction-coffee').value);
        const gramsOfWater = parseInt(document.getElementById('new-instruction-water').value);
        const instructionSteps = document.getElementById('new-instruction-steps').value.trim();

        if (!brewMethod || !waterTemp || !grindSize || !gramsOfCoffee || !gramsOfWater || !instructionSteps) {
            this.showError('Please fill in all instruction fields');
            return;
        }

        const instructionData = {
            brewMethod,
            waterTemp,
            grindSize,
            gramsOfCoffee,
            gramsOfWater,
            instructionSteps
        };

        try {
            const response = await fetch('/api/instructions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                },
                body: JSON.stringify(instructionData)
            });

            if (response.ok) {
                const newInstruction = await response.json();
                this.selectInstruction(newInstruction);
                this.clearNewInstructionForm();
                this.showSuccess('Instructions created successfully!');
                // Reload instructions to show the new one
                await this.loadInstructions();
            } else {
                const error = await response.text();
                this.showError(`Failed to create instructions: ${error}`);
            }
        } catch (error) {
            console.error('Error creating instructions:', error);
            this.showError('Error creating instructions');
        }
    }

    selectBean(bean) {
        this.selectedBean = bean;

        // Update UI
        document.querySelectorAll('.bean-item').forEach(item => {
            item.classList.remove('border-amber-500', 'bg-amber-100');
            item.classList.add('border-gray-200');
        });

        if (bean.id) {
            const selectedItem = document.querySelector(`[data-bean-id="${bean.id}"]`);
            if (selectedItem) {
                selectedItem.classList.add('border-amber-500', 'bg-amber-100');
                selectedItem.classList.remove('border-gray-200');
            }
        }

        // Show preview
        const preview = document.getElementById('selected-bean-preview');
        const content = document.getElementById('bean-preview-content');
        content.innerHTML = `
            <p><strong>Flavor:</strong> ${esc(bean.flavor)}</p>
            <p><strong>Origin:</strong> ${esc(bean.origin)}</p>
            <p><strong>Roast:</strong> ${esc(bean.roast)}</p>
        `;
        preview.classList.remove('hidden');

        // Enable next button
        document.getElementById('step1-next').disabled = false;
    }

    selectInstruction(instruction) {
        this.selectedInstruction = instruction;

        // Update UI
        document.querySelectorAll('.instruction-item').forEach(item => {
            item.classList.remove('border-amber-500', 'bg-amber-100');
            item.classList.add('border-gray-200');
        });

        if (instruction.id) {
            const selectedItem = document.querySelector(`[data-instruction-id="${instruction.id}"]`);
            if (selectedItem) {
                selectedItem.classList.add('border-amber-500', 'bg-amber-100');
                selectedItem.classList.remove('border-gray-200');
            }
        }

        // Show preview
        const preview = document.getElementById('selected-instruction-preview');
        const content = document.getElementById('instruction-preview-content');
        content.innerHTML = `
            <p><strong>Brew Method:</strong> ${esc(instruction.brewMethod)}</p>
            <p><strong>Coffee:</strong> ${Number(instruction.gramsOfCoffee)}g | <strong>Water:</strong> ${Number(instruction.gramsOfWater)}g</p>
            <p><strong>Water Temp:</strong> ${Number(instruction.waterTemp)}°F | <strong>Grind:</strong> ${esc(instruction.grindSize)}</p>
            <p><strong>Steps:</strong> ${esc(instruction.instructionSteps.substring(0, 150))}${instruction.instructionSteps.length > 150 ? '...' : ''}</p>
        `;
        preview.classList.remove('hidden');

        // Enable next button
        document.getElementById('step2-next').disabled = false;
    }

    goToStep(step) {
        // Hide all steps
        document.querySelectorAll('.step-content').forEach(content => {
            content.classList.add('hidden');
        });

        // Show current step
        document.getElementById(`step${step}`).classList.remove('hidden');

        // Update progress indicators
        this.updateProgressIndicators(step);

        // Load data for step 2 if needed
        if (step === 2 && this.instructions.length === 0) {
            this.loadInstructions();
        }

        // Update final summaries for step 3
        if (step === 3) {
            this.updateFinalSummaries();
        }

        this.currentStep = step;
    }

    updateProgressIndicators(currentStep) {
        for (let i = 1; i <= 3; i++) {
            const indicator = document.getElementById(`step${i}-indicator`);
            const isActive = i <= currentStep;
            const isCompleted = i < currentStep;

            if (isCompleted) {
                indicator.className = 'w-10 h-10 bg-green-600 text-white rounded-full flex items-center justify-center font-bold';
            } else if (isActive) {
                indicator.className = 'w-10 h-10 bg-amber-600 text-white rounded-full flex items-center justify-center font-bold';
            } else {
                indicator.className = 'w-10 h-10 bg-gray-300 text-gray-600 rounded-full flex items-center justify-center font-bold';
            }
        }

        // Update progress bars
        const progress1 = document.getElementById('progress1');
        const progress2 = document.getElementById('progress2');

        progress1.className = currentStep > 1 ? 'h-1 w-16 bg-green-600' : 'h-1 w-16 bg-gray-300';
        progress2.className = currentStep > 2 ? 'h-1 w-16 bg-green-600' : 'h-1 w-16 bg-gray-300';
    }

    updateFinalSummaries() {
        if (this.selectedBean) {
            document.getElementById('final-bean-summary').innerHTML = `
                <p><strong>Flavor:</strong> ${esc(this.selectedBean.flavor)}</p>
                <p><strong>Origin:</strong> ${esc(this.selectedBean.origin)}</p>
                <p><strong>Roast:</strong> ${esc(this.selectedBean.roast)}</p>
            `;
        }

        if (this.selectedInstruction) {
            document.getElementById('final-instruction-summary').innerHTML = `
                <p><strong>Brew Method:</strong> ${esc(this.selectedInstruction.brewMethod)}</p>
                <p><strong>Ratio:</strong> ${Number(this.selectedInstruction.gramsOfCoffee)}g coffee to ${Number(this.selectedInstruction.gramsOfWater)}g water</p>
                <p><strong>Water Temp:</strong> ${Number(this.selectedInstruction.waterTemp)}°F</p>
                <p><strong>Grind Size:</strong> ${esc(this.selectedInstruction.grindSize)}</p>
            `;
        }
    }

    async saveRecipe() {
        if (!this.selectedBean || !this.selectedInstruction) {
            this.showError('Please select both a bean and instructions');
            return;
        }

        const title = document.getElementById('recipe-title').value.trim();
        const notes = document.getElementById('recipe-notes').value.trim();

        const recipeData = {
            beanId: this.selectedBean.id,
            instructionId: this.selectedInstruction.id,
            title: title || null,
            notes: notes || null
        };

        try {
            const response = await fetch('/api/recipes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                    // Remove Authorization header for session-based auth
                },
                body: JSON.stringify(recipeData)
            });

            if (response.ok) {
                this.showSuccessModal();
            } else {
                const error = await response.text();
                this.showError(`Failed to save recipe: ${error}`);
            }
        } catch (error) {
            console.error('Error saving recipe:', error);
            this.showError('Error saving recipe');
        }
    }

    showSuccessModal() {
        document.getElementById('success-modal').classList.remove('hidden');
    }

    resetForm() {
        // Hide modal
        document.getElementById('success-modal').classList.add('hidden');

        // Reset all form data
        this.selectedBean = null;
        this.selectedInstruction = null;
        this.currentStep = 1;

        // Clear forms
        this.clearNewBeanForm();
        this.clearNewInstructionForm();
        document.getElementById('recipe-title').value = '';
        document.getElementById('recipe-notes').value = '';

        // Hide previews
        document.getElementById('selected-bean-preview').classList.add('hidden');
        document.getElementById('selected-instruction-preview').classList.add('hidden');

        // Reset to step 1
        this.goToStep(1);

        // Disable next buttons
        document.getElementById('step1-next').disabled = true;
        document.getElementById('step2-next').disabled = true;

        // Clear search fields
        document.getElementById('bean-search').value = '';
        document.getElementById('instruction-search').value = '';

        // Reload data
        this.loadBeans();
    }

    clearNewBeanForm() {
        document.getElementById('new-bean-flavor').value = '';
        document.getElementById('new-bean-origin').value = '';
        document.getElementById('new-bean-roast').value = '';
    }

    clearNewInstructionForm() {
        document.getElementById('new-instruction-brewmethod').value = '';
        document.getElementById('new-instruction-watertemp').value = '';
        document.getElementById('new-instruction-grindsize').value = '';
        document.getElementById('new-instruction-coffee').value = '';
        document.getElementById('new-instruction-water').value = '';
        document.getElementById('new-instruction-steps').value = '';
    }

    getAuthToken() {
        // For session-based auth, we don't need to send a token
        // The session cookie will be sent automatically
        return '';
    }

    showError(message) {
        // Simple alert for now - could be enhanced with a proper toast notification
        alert('Error: ' + message);
    }

    showSuccess(message) {
        // Simple alert for now - could be enhanced with a proper toast notification
        alert('Success: ' + message);
    }
}

// Initialize the recipe creator when the page loads
document.addEventListener('DOMContentLoaded', () => {
    new RecipeCreator();
});
