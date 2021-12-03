module.exports = class TestPlugin1 {
    render(parent, _slot) {
        const div = document.createElement('div');
        div.innerHTML = '<h3>Test Plugin 1</h3>';
        parent.appendChild(div);
    }
};
